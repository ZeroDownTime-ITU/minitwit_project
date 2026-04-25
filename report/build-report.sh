#!/usr/bin/env bash
# build-report.sh — converts report.md to a PDF using pandoc.
#
# Usage: ./build-report.sh
# Output: report.pdf in the current directory.
#
# Works on macOS, Linux, and Windows (via Git Bash or WSL).
# See README.md for installation requirements.

set -euo pipefail

cd "$(dirname "$0")"

INPUT="report.md"
OUTPUT="report.pdf"
TEMPLATE="./template.tex"

if ! command -v pandoc >/dev/null 2>&1; then
    echo "Error: pandoc is not installed. See report/README.md."
    echo "  macOS:    brew install pandoc"
    echo "  Ubuntu:   sudo apt install pandoc"
    echo "  Windows:  https://pandoc.org/installing.html"
    exit 1
fi

if ! command -v xelatex >/dev/null 2>&1; then
    echo "Error: xelatex is not installed or not on PATH."
    echo "See report/README.md for installation and PATH setup."
    exit 1
fi

echo "Building $OUTPUT from $INPUT..."

pandoc "$INPUT" \
    --output="$OUTPUT" \
    --template="$TEMPLATE" \
    --number-sections \
    --pdf-engine=xelatex \
    --syntax-highlighting=tango

echo "Done. Output: $(pwd)/$OUTPUT"