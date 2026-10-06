package com.example

import com.example.data.DefaultData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDemoProjectInitialized() {
        val project = DefaultData.demoProject
        assertEquals("The Moonlight Adventure", project.title)
        assertEquals(5, project.scenes.size)
        assertEquals(2, project.characters.size)
        assertNotNull(project.scenes.first().imageResId)
    }

    @Test
    fun testTemplatesAvailable() {
        val templates = DefaultData.templates
        assertTrue(templates.size >= 10)
        assertTrue(templates.any { it.title == "Kids Story" })
        assertTrue(templates.any { it.title == "Horror Story" })
        assertTrue(templates.any { it.title == "TikTok / Reels" })
    }
}
