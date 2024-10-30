package com.avwaveaf.storyspace.utils

import com.avwaveaf.storyspace.data.model.ListStoryItem

object DataDummy {

    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = i.toString(),
                createdAt = "$i, december, 2024",
                name = "john $i",
                description = "nothing"
            )
            items.add(story)
        }
        return items
    }
}