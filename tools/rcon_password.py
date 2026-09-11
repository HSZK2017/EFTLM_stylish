"""RCON 凭据解析（唯一实现，P0 修复 2026-09-10）。

背景：密码此前硬编码在 5 个脚本的默认值里、以明文命令行参数传递，
并在 README 中公开；RCON 权限等同服务器控制权。

解析优先级（不再有硬编码默认值）：
    1. 显式 CLI 参数
    2. 环境变量 EFTLM_RCON_PASSWORD
    3. <script_dir>/rcon_password.txt
    4. <repo>/tools/rcon_password.txt
    5. <repo>/config/rcon_password.txt

未找到时返回空串，由调用方报错退出（fail closed）。密码文件不入库（见 .gitignore）。
"""

from __future__ import annotations

import os

ENV_VAR = "EFTLM_RCON_PASSWORD"
FILE_NAME = "rcon_password.txt"


def _read_file(path: str) -> str:
    try:
        with open(path, "r", encoding="utf-8") as f:
            return f.read().strip()
    except OSError:
        return ""


def resolve(cli_value: str | None = None, script_dir: str | None = None) -> str:
    """按优先级解析 RCON 密码；找不到返回空串。"""
    if cli_value:
        return cli_value.strip()
    env = os.environ.get(ENV_VAR, "")
    if env.strip():
        return env.strip()
    candidates = []
    if script_dir:
        candidates.append(os.path.join(script_dir, FILE_NAME))
    here = os.path.dirname(os.path.abspath(__file__))
    candidates.append(os.path.join(here, FILE_NAME))
    candidates.append(os.path.join(os.path.dirname(here), "config", FILE_NAME))
    for path in candidates:
        value = _read_file(path)
        if value:
            return value
    return ""


def require(cli_value: str | None = None, script_dir: str | None = None) -> str:
    """同 resolve()，但找不到时打印明确指引并终止（fail closed）。"""
    value = resolve(cli_value, script_dir)
    if not value:
        raise SystemExit(
            "[rcon] RCON password not configured. Set one of:\n"
            f"  1) --rcon-password <pw>\n"
            f"  2) environment variable {ENV_VAR}\n"
            f"  3) file tools/{FILE_NAME} (one line, not committed)\n"
            "Refusing to run with an empty/hardcoded password."
        )
    return value
