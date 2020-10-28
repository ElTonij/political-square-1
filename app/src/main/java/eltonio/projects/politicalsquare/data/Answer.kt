package eltonio.projects.politicalsquare.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "Answers")
data class Answer(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Int,
    val answer_uk: String,
    val answer_ru: String,
    val answer_en: String
)