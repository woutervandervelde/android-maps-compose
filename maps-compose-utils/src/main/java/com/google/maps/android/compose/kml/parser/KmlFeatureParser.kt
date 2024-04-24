package com.google.maps.android.compose.kml.parser

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.kml.data.KmlTags.Companion.DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.DESCRIPTION_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.DISPLAY_NAME_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EXTENDED_DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.NAME_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_URL_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.TESSELLATE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VALUE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VISIBILITY_TAG
import org.xmlpull.v1.XmlPullParser

internal abstract class KmlFeatureParser {
    companion object {
        /**
         * Extracts data from KML ExtendedData tag, returns a list of [ExtendedData] containing all Data tags inside the ExtendedData
         *
         * @param parser XmlPullParser containing KML ExtendedData tag
         * @return list of [ExtendedData] containing name, displayName and value from data and value tags
         */
        internal fun parseExtendedData(parser: XmlPullParser): List<ExtendedData> {
            val extendedData: MutableList<ExtendedData> = mutableListOf()
            var currentData = ExtendedData.empty()
            var eventType = parser.eventType

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(EXTENDED_DATA_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        DATA_TAG ->
                            currentData.name = parser.getAttributeValue(null, NAME_TAG)

                        DISPLAY_NAME_TAG ->
                            currentData.displayName = parser.nextText()

                        VALUE_TAG -> {
                            currentData.value = parser.nextText()
                            extendedData.add(currentData)
                            currentData = ExtendedData.empty()
                        }
                    }
                }
                eventType = parser.next()
            }

            return extendedData
        }

        /**
         *
         */
        internal fun parseCoordinates(input: String): List<LatLng> {
            return input.trim().split("\n").map {
                val coordinate = it.split(LAT_LNG_ALT_SEPARATOR)
                val lat = coordinate[LATITUDE_INDEX].toDouble()
                val lng = coordinate[LONGITUDE_INDEX].toDouble()
                LatLng(lat, lng)
            }
        }

        internal val PROPERTY_REGEX =
            Regex("$NAME_TAG|$DESCRIPTION_TAG|drawOrder|$VISIBILITY_TAG|address|phoneNumber|$STYLE_URL_TAG|$TESSELLATE_TAG")
        private const val LONGITUDE_INDEX = 0
        private const val LATITUDE_INDEX = 1
        private const val LAT_LNG_ALT_SEPARATOR = ","
    }
}