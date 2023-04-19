package com.example.audiorecorder

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "audioRecords")
data class AudioRecord(
    var filename: String,
    var filePath: String,
    var timeStamp: Long,
    var duration: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    // will not be stored in database
    @Ignore
    var isChecked = false

}