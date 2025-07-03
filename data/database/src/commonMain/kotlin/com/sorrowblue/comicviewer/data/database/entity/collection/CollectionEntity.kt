package com.sorrowblue.comicviewer.data.database.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlinx.datetime.parse
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
@Entity(
    tableName = "collection",
    foreignKeys = [
        ForeignKey(
            entity = BookshelfEntity::class,
            parentColumns = [BookshelfEntity.ID],
            childColumns = [CollectionEntity.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(CollectionEntity.BOOKSHELF_ID)]
)
internal data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: CollectionId,
    val name: String,
    val type: Type,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: BookshelfId?,
    @Embedded val searchCondition: SearchConditionEntity?,
    @ColumnInfo("created_at", defaultValue = "CURRENT_TIMESTAMP") val createdAt: String,
    @ColumnInfo("updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: String,
) {

    enum class Type {
        Smart,
        Basic,
    }

    fun toModel(count: Int): Collection {
        return when (type) {
            Type.Smart -> SmartCollection(
                id = id,
                name = name,
                bookshelfId = bookshelfId,
                searchCondition = searchCondition?.toModel() ?: SearchCondition(),
                count = count,
                createdAt = Instant.parse(createdAt, SQLITE_DATETIME_FORMAT)
                    .toLocalDateTime(TimeZone.UTC),
                updatedAt = Instant.parse(updatedAt, SQLITE_DATETIME_FORMAT)
                    .toLocalDateTime(TimeZone.UTC)
            )

            Type.Basic -> BasicCollection(
                id = id,
                name = name,
                count = count,
                createdAt = Instant.parse(createdAt, SQLITE_DATETIME_FORMAT)
                    .toLocalDateTime(TimeZone.UTC),
                updatedAt = Instant.parse(updatedAt, SQLITE_DATETIME_FORMAT)
                    .toLocalDateTime(TimeZone.UTC)
            )
        }
    }

    companion object {

        const val ID = "id"
        const val BOOKSHELF_ID = "bookshelf_id"

        fun fromModel(model: Collection): CollectionEntity {
            return when (model) {
                is BasicCollection -> CollectionEntity(
                    id = model.id,
                    name = model.name,
                    type = Type.Basic,
                    bookshelfId = null,
                    searchCondition = null,
                    createdAt = "",
                    updatedAt = ""
                )

                is SmartCollection -> CollectionEntity(
                    id = model.id,
                    name = model.name,
                    type = Type.Smart,
                    bookshelfId = model.bookshelfId,
                    searchCondition = SearchConditionEntity.fromModel(model.searchCondition),
                    createdAt = "",
                    updatedAt = ""
                )
            }
        }

        // YYYY-MM-DD HH:MM:SS
        private val SQLITE_DATETIME_FORMAT = DateTimeComponents.Format {
            year()
            char('-')
            monthNumber()
            char('-')
            day()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }
}
