/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.book

import com.sorrowblue.comicviewer.domain.model.file.Book

/**
 * ビューアのページアイテムを表す基底インターフェース。
 */
sealed interface PageItem {
    val key: String
}

/**
 * 前後の本への遷移案内ページ。
 *
 * @property isNext 次の本への案内であれば true、前の本であれば false
 * @property nextBooks 候補となる次の本の一覧
 */
data class NextPage(val isNext: Boolean, val nextBooks: List<NextBook>) : PageItem {
    override val key: String = "next:$isNext"
}

/**
 * 前後の本を表すインターフェース。
 */
sealed interface NextBook {
    val book: Book

    /** フォルダー内の前後の本 */
    data class Folder(override val book: Book) : NextBook

    /** コレクション内の前後の本 */
    data class Collection(override val book: Book) : NextBook
}

/**
 * 画像サイズ（縦横比）が未評価で、レイアウト未確定のページを表すマーカーインターフェース。
 */
sealed interface UnratedPage

/**
 * コミックの各ページを表すインターフェース。
 */
sealed interface BookPage : PageItem {

    /**
     * 読み取ったページをそのまま表示するデフォルト形式。
     *
     * @property index ページインデックス（0始まり）
     */
    data class Default(val index: Int) : BookPage {
        override val key: String = "Default:$index"
    }

    /**
     * 見開き表示形式。縦長画像は隣接ページと結合され、横長画像は単独見開きとなる。
     */
    sealed interface Spread : BookPage {
        val index: Int

        /** 画像サイズ未評価の状態 */
        data class Unrated(override val index: Int) :
            Spread,
            UnratedPage {
            override val key: String = "Spread:$index"
        }

        /** 2つの縦長ページが結合された状態 */
        data class Combine(override val index: Int, val nextIndex: Int) : Spread {
            override val key: String = "Spread:$index:$nextIndex"
        }

        /** 表紙や奇数残りなどの単一ページ状態 */
        data class Single(override val index: Int) : Spread {
            override val key: String = "Spread:$index"
        }

        /** 単体で横長のページ（見開き相当） */
        data class Spread2(override val index: Int) : Spread {
            override val key: String = "Spread:$index"
        }
    }

    /**
     * 分割表示形式。横長画像は左右の2ページに分割される。
     */
    sealed interface Split : BookPage {
        val index: Int

        /** 画像サイズ未評価の状態 */
        data class Unrated(override val index: Int) :
            Split,
            UnratedPage {
            override val key: String = "Split:$index"
        }

        /** 縦長のため分割不要な単一ページ状態 */
        data class Single(override val index: Int) : Split {
            override val key: String = "Split:$index"
        }

        /** 横長画像の左半分 */
        data class Left(override val index: Int) : Split {
            override val key: String = "Split:$index"
        }

        /** 横長画像の右半分 */
        data class Right(override val index: Int) : Split {
            override val key: String = "Split:$index:r"
        }
    }
}
