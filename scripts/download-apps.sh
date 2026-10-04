#!/usr/bin/env bash
# Downloads the Sauce Labs "My Demo App" builds used by the examples into examples/apps/.
# The apps are NOT redistributed by this repository: they are fetched from the official Sauce Labs releases.
set -euo pipefail

VERSION="${MDA_VERSION:-2.3.0}"
ANDROID_BUILD="${MDA_ANDROID_BUILD:-27}"
TARGET="$(cd "$(dirname "$0")/.." && pwd)/examples/apps"
mkdir -p "$TARGET"

download() {
  local url="$1" file="$2"
  if [[ -f "$TARGET/$file" ]]; then
    echo "already present: $file"
  else
    echo "downloading $file"
    curl -fL --retry 3 -o "$TARGET/$file.part" "$url"
    mv "$TARGET/$file.part" "$TARGET/$file"
  fi
}

WHAT="${1:-all}"
if [[ "$WHAT" != "android" && "$WHAT" != "ios" && "$WHAT" != "all" ]]; then
  echo "usage: $0 [android|ios|all]"
  exit 1
fi
if [[ "$WHAT" == "android" || "$WHAT" == "all" ]]; then
  download "https://github.com/saucelabs/my-demo-app-android/releases/download/$VERSION/mda-$VERSION-$ANDROID_BUILD.apk" "mda-$VERSION-$ANDROID_BUILD.apk"
fi
if [[ "$WHAT" == "ios" || "$WHAT" == "all" ]]; then
  download "https://github.com/saucelabs/my-demo-app-ios/releases/download/$VERSION/SauceLabs-Demo-App.Simulator.zip" "SauceLabs-Demo-App.Simulator.zip"
fi
echo "apps in $TARGET"
