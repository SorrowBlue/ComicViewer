import re
import sys

def main():
    if len(sys.argv) < 3:
        print("Usage: python extract_deps.py <input_conveyor_conf> <output_manifest_txt>")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    dependencies = []
    
    # Gradle のキャッシュパス (files-2.1) から Group:Artifact:Version を抽出する正規表現
    # 例: files-2.1/org.jetbrains.skiko/skiko-awt-runtime-windows-x64/0.144.6/...
    pattern = re.compile(r"files-2\.1[/\\]([^/\\]+)[/\\]([^/\\]+)[/\\]([^/\\]+)")

    try:
        with open(input_file, 'r', encoding='utf-8') as f:
            for line in f:
                match = pattern.search(line)
                if match:
                    group = match.group(1)
                    artifact = match.group(2)
                    version = match.group(3)
                    dependencies.append(f"{group}:{artifact}:{version}")
    except Exception as e:
        print(f"Error reading file {input_file}: {e}")
        sys.exit(1)

    # 重複を排除してアルファベット順にソート
    unique_deps = sorted(list(set(dependencies)))

    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            for dep in unique_deps:
                f.write(dep + "\n")
        print(f"Successfully extracted {len(unique_deps)} dependencies to {output_file}")
    except Exception as e:
        print(f"Error writing file {output_file}: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
