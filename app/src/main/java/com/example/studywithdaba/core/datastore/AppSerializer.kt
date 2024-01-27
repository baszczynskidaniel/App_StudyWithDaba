package com.example.studywithdaba.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.studywithdaba.core.datastore.model.UserData
import com.example.studywithdaba.core.datastore.proto.UserPreferences
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            // readFrom is already called on the data store background thread
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}