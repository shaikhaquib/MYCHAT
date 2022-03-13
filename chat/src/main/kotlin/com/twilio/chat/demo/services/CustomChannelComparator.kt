package com.twilio.chat.demo.services

import com.twilio.chat.demo.ChannelModel
import java.util.Comparator

public class CustomChannelComparator : Comparator<ChannelModel> {
    override fun compare(lhs: ChannelModel, rhs: ChannelModel): Int {
        return lhs.friendlyName.compareTo(rhs.friendlyName)
    }
}
