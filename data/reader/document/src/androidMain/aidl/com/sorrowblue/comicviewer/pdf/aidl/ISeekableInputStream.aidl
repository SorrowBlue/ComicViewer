package com.sorrowblue.comicviewer.pdf.aidl;

// このインターフェースは別のAIDLファイルから利用される可能性があるため、宣言しておくと良い
// onewayは非同期呼び出しで戻り値がない場合に使う。今回は戻り値があるので使わない。
interface ISeekableInputStream {

    /**
     * バッファにデータを読み込む。
     * bufはクライアントから渡され、サービス側で中身が変更されるため inout を指定する。
     * @param buf データを格納するバイト配列
     * @return 読み込んだバイト数。ストリームの終端に達した場合は-1。
     */
    int read(inout byte[] buf);

    /**
     * ストリームの読み込み位置を変更する。
     * @param offset オフセット
     * @param whence 基準点 (SEEK_SET, SEEK_CUR, SEEK_END)
     * @return 結果のオフセット位置
     */
    long seek(long offset, int whence);

    /**
     * 現在の読み込み位置を取得する。
     * @return 現在位置
     */
    long position();

    /**
     * AutoCloseableの代わり。リソースを解放するために明示的に呼び出す。
     * リモートオブジェクトなので、使い終わったら必ず呼ぶ必要がある。
     */
    void close();
}
