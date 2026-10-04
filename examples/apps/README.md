# Apps under test

App binaries are **not** committed (they are large and the demo apps are not ours to redistribute).

Download the Sauce Labs "My Demo App" builds used by the examples:

```bash
./scripts/download-apps.sh            # macOS / Linux
```
```powershell
./scripts/download-apps.ps1           # Windows
```

For your own app, point `app.path` (in `src/test/resources/env/*.properties`) to your build output instead.
