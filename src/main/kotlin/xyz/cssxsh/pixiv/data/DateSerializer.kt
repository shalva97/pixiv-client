package xyz.cssxsh.pixiv.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface DateSerializer : KSerializer<LocalDate> {

    val dateFormat: DateTimeFormatter

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateSerializerTo[${dateFormat}]", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate): Unit = encoder.encodeString(value.format(dateFormat))

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString(), dateFormat)
}