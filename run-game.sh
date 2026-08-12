#!/usr/bin/env bash

set -Eeuo pipefail

PROJECT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd -P)"
PORT="${SERVER_PORT:-19150}"
WRAPPER_JAR="$PROJECT_DIR/gradle/wrapper/gradle-wrapper.jar"

find_java_21() {
    local candidate
    local version
    local candidates=()

    if [[ -n "${JAVA_HOME:-}" ]]; then
        candidates+=("$JAVA_HOME/bin/java")
    fi

    if command -v java >/dev/null 2>&1; then
        candidates+=("$(command -v java)")
    fi

    candidates+=(
        /usr/lib/jvm/java-21-openjdk-amd64/bin/java
        /usr/lib/jvm/java-21-openjdk-arm64/bin/java
    )

    for candidate in "${candidates[@]}"; do
        [[ -x "$candidate" ]] || continue
        version="$("$candidate" -version 2>&1 | awk -F '[".]' '/version/ { print $2; exit }')"
        if [[ "$version" == "21" ]]; then
            JAVA_CMD="$(readlink -f "$candidate")"
            export JAVA_HOME="$(cd -- "$(dirname -- "$JAVA_CMD")/.." >/dev/null 2>&1 && pwd -P)"
            return 0
        fi
    done

    return 1
}

port_pids() {
    if command -v lsof >/dev/null 2>&1; then
        lsof -nP -t -iTCP:"$PORT" -sTCP:LISTEN 2>/dev/null || true
    elif command -v fuser >/dev/null 2>&1; then
        fuser "$PORT/tcp" 2>/dev/null | tr ' ' '\n' | awk '/^[0-9]+$/' || true
    elif command -v ss >/dev/null 2>&1; then
        ss -ltnp "sport = :$PORT" 2>/dev/null \
            | grep -oE 'pid=[0-9]+' \
            | cut -d= -f2 \
            || true
    else
        echo "ERROR: Cần cài lsof, psmisc hoặc iproute2 để kiểm tra cổng $PORT." >&2
        exit 1
    fi
}

stop_old_server() {
    local pids=()
    local pid
    local deadline

    mapfile -t pids < <(port_pids | sort -u)
    if (( ${#pids[@]} == 0 )); then
        echo "Cổng $PORT đang trống."
        return
    fi

    for pid in "${pids[@]}"; do
        [[ "$pid" =~ ^[0-9]+$ ]] || continue
        if (( pid <= 1 )); then
            echo "ERROR: Từ chối dừng PID không an toàn: $pid" >&2
            exit 1
        fi
        echo "Đang dừng PID $pid dùng cổng $PORT..."
        kill "$pid" 2>/dev/null || {
            echo "ERROR: Không thể dừng PID $pid. Hãy chạy bằng quyền phù hợp." >&2
            exit 1
        }
    done

    deadline=$((SECONDS + 10))
    while (( SECONDS < deadline )); do
        mapfile -t pids < <(port_pids | sort -u)
        (( ${#pids[@]} == 0 )) && return
        sleep 1
    done

    for pid in "${pids[@]}"; do
        [[ "$pid" =~ ^[0-9]+$ ]] || continue
        echo "Buộc dừng PID $pid..."
        kill -KILL "$pid" 2>/dev/null || true
    done

    sleep 1
    mapfile -t pids < <(port_pids | sort -u)
    if (( ${#pids[@]} > 0 )); then
        echo "ERROR: Cổng $PORT vẫn đang được sử dụng." >&2
        exit 1
    fi
}

if ! find_java_21; then
    cat >&2 <<'EOF'
ERROR: Không tìm thấy JDK 21.
Cài trên Ubuntu bằng lệnh:
  sudo apt update
  sudo apt install -y openjdk-21-jdk
EOF
    exit 1
fi

if [[ ! -f "$WRAPPER_JAR" ]]; then
    echo "ERROR: Không tìm thấy $WRAPPER_JAR" >&2
    exit 1
fi

cd "$PROJECT_DIR"

echo "[1/2] Kiểm tra cổng $PORT..."
stop_old_server

echo "[2/2] Khởi động server..."
echo "JAVA_HOME=$JAVA_HOME"

exec "$JAVA_CMD" \
    -Dfile.encoding=UTF-8 \
    -Xms64m \
    -Xmx64m \
    -Dorg.gradle.appname=gradlew \
    -classpath "" \
    -jar "$WRAPPER_JAR" \
    run "$@"