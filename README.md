# simplemodeling-model

SimpleModeling.org の model 専用プロジェクト。

## 方針

- 基本部は `goldenport-core` から段階的に移管する
- 新規開発クラスの多くは `cml` から自動生成される Value Object を利用する
- 手書きコードと自動生成コードの責務を分離する

## ディレクトリ構成

- `src/main/scala`: 手書きのモデル基盤・共通ロジック
- `src/main/cozy`: `cozyGenerate` の入力モデル（`.cml`）
- `target/...`: `cozy` による自動生成コードの配置先（ビルド生成物）
- `src/test/scala`: テスト
- `docs`: 運用メモ・移管計画

## 開発フロー（暫定）

1. `goldenport-core` から移管対象の基盤クラスを `src/main/scala` に移す
2. `cml` から Value Object を生成し `target` 配下に出力する（`cozy`）
3. 必要に応じて手書きコード側から generated の型を利用する
4. `sbt test` で回帰確認する

`cozy` 生成は以下を想定:

```bash
sbt cozyGenerate
```

## 次に着手する項目

- `goldenport-core` 移管対象の優先順位を確定
- `cml` 生成コマンド（またはスクリプト）をリポジトリ内に固定化
- generated コードの再生成手順を CI へ組み込む
