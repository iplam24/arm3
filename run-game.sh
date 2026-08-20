#!/usr/bin/env bash

set -Eeuo pipefail

PROJECT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd -P)"
PORT="${SERVER_PORT:-19150}"
GRADLE_VERSION="8.14.2"
GRADLE_DIST_DIR="$PROJECT_DIR/.gradle-dist"
GRADLE_HOME="$GRADLE_DIST_DIR/gradle-$GRADLE_VERSION"
GRADLE_ZIP="$GRADLE_DIST_DIR/gradle-$GRADLE_VERSION-bin.zip"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"

find_java_21() {
    local candidate
    local version
    local candidates=()

    [[ -n "${JAVA_HOME:-}" ]] && candidates+=("$JAVA_HOME/bin/java")
    command -v java >/dev/null 2>&1 && candidates+=("$(command -v java)")
    candidates+=(
        /usr/lib/jvm/java-21-openjdk-amd64/bin/java
        /usr/lib/jvm/java-21-openjdk-arm64/bin/java
    )

    for candidate in "${candidates[@]}"; do
        [[ -x "$candidate" ]] || continue
        version="$("$candidate" -version 2>&1 | awk -F '[".]' '/version/ { print $2; exit }')"
        [[ "$version" == "21" ]] || continue

        JAVA_CMD="$(readlink -f "$candidate")"
        export JAVA_HOME="$(cd -- "$(dirname -- "$JAVA_CMD")/.." >/dev/null 2>&1 && pwd -P)"
        return 0
    done

    return 1
}

port_pids() {
    if command -v lsof >/dev/null 2>&1; then
        lsof -nP -t -iTCP:"$PORT" -sTCP:LISTEN 2>/dev/null || true
    elif command -v fuser >/dev/null 2>&1; then
        fuser "$PORT/tcp" 2>/dev/null | tr ' ' '\n' | awk '/^[0-9]+$/' || true
    elif command -v ss >/dev/null 2>&1; then
        ss -ltnp 2>/dev/null \
            | awk -v port=":$PORT" '$4 ~ port "$" { print $0 }' \
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

find_server_jar() {
    local jar
    local candidates=(
        "$PROJECT_DIR/vxldeptrai.jar"
        "$PROJECT_DIR/build/libs/vxldeptrai.jar"
        "$PROJECT_DIR/mobiarmy3.jar"
        "$PROJECT_DIR/build/libs/mobiarmy3.jar"
        "$PROJECT_DIR/build/libs/LoCheo3-1.0.0.jar"
    )

    for jar in "${candidates[@]}"; do
        if [[ -s "$jar" ]]; then
            SERVER_JAR="$jar"
            return 0
        fi
    done

    return 1
}

download_gradle() {
    local partial="$GRADLE_ZIP.part"
    local url
    local urls=(
        "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
        "https://downloads.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
    )

    command -v unzip >/dev/null 2>&1 || {
        echo "ERROR: Thiếu unzip. Cài bằng: sudo apt install -y unzip" >&2
        exit 1
    }

    mkdir -p "$GRADLE_DIST_DIR"

    if [[ -f "$GRADLE_ZIP" ]] && unzip -tq "$GRADLE_ZIP" >/dev/null 2>&1; then
        return
    fi

    rm -f -- "$GRADLE_ZIP"

    for url in "${urls[@]}"; do
        echo "Đang tải Gradle $GRADLE_VERSION từ: $url"

        if command -v curl >/dev/null 2>&1; then
            curl \
                --fail \
                --location \
                --continue-at - \
                --retry 10 \
                --retry-delay 3 \
                --retry-all-errors \
                --connect-timeout 30 \
                --max-time 1800 \
                --output "$partial" \
                "$url" || true
        elif command -v wget >/dev/null 2>&1; then
            wget \
                --continue \
                --tries=10 \
                --timeout=30 \
                --output-document="$partial" \
                "$url" || true
        else
            echo "ERROR: Thiếu curl hoặc wget." >&2
            echo "Cài bằng: sudo apt install -y curl unzip" >&2
            exit 1
        fi

        if [[ -f "$partial" ]] && unzip -tq "$partial" >/dev/null 2>&1; then
            mv -f -- "$partial" "$GRADLE_ZIP"
            return
        fi

        echo "File tải chưa hoàn chỉnh, chuyển sang nguồn dự phòng..."
        rm -f -- "$partial"
    done

    echo "ERROR: Không tải được Gradle $GRADLE_VERSION sau nhiều lần thử." >&2
    exit 1
}

prepare_gradle() {
    if [[ -x "$GRADLE_BIN" ]]; then
        return
    fi

    download_gradle
    echo "Đang giải nén Gradle $GRADLE_VERSION..."
    unzip -oq "$GRADLE_ZIP" -d "$GRADLE_DIST_DIR"

    if [[ ! -x "$GRADLE_BIN" ]]; then
        echo "ERROR: Không tìm thấy Gradle sau khi giải nén: $GRADLE_BIN" >&2
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

cd "$PROJECT_DIR"

echo "[1/2] Kiểm tra cổng $PORT..."
stop_old_server

echo "[2/2] Khởi động server..."
echo "JAVA_HOME=$JAVA_HOME"

if find_server_jar; then
    echo "Chạy JAR: $SERVER_JAR"
    exec "$JAVA_CMD" \
        -Dfile.encoding=UTF-8 \
        -Dstdout.encoding=UTF-8 \
        -Dstderr.encoding=UTF-8 \
        -Xms512M \
        -Xmx1024M \
        -jar "$SERVER_JAR" \
        "$@"
fi

echo "Không có JAR sẵn, tiến hành build/chạy bằng Gradle..."
prepare_gradle

"$GRADLE_BIN" --no-daemon --console=plain clean jar

if ! find_server_jar; then
    echo "ERROR: Build completed but server JAR was not found." >&2
    exit 1
fi

echo "Starting built JAR: $SERVER_JAR"
exec "$JAVA_CMD" \
    -Dfile.encoding=UTF-8 \
    -Dstdout.encoding=UTF-8 \
    -Dstderr.encoding=UTF-8 \
    -Xms512M \
    -Xmx1024M \
    -jar "$SERVER_JAR" \
    "$@"
