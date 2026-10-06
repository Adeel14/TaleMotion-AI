package com.example.data

import com.example.R
import com.example.model.Character
import com.example.model.LanguageOption
import com.example.model.MusicMood
import com.example.model.Project
import com.example.model.ProjectStatus
import com.example.model.Scene
import com.example.model.StoryTemplate
import com.example.model.VideoFormat
import com.example.model.VisualStyle
import com.example.model.VoiceType

object DefaultData {

    val demoCharacters = listOf(
        Character(
            id = "char_ayan",
            name = "Ayan",
            age = "12",
            gender = "Male",
            appearance = "A curious young explorer boy with sparkling amber eyes and an adventurous grin",
            hair = "Wind-tousled messy dark brown hair with soft front bangs",
            clothes = "Warm hooded adventure vest, teal utility jacket, sturdy brown trekking boots, brass compass",
            personality = "Brave, compassionate, deeply curious about nature's secrets",
            voiceType = "Young voice",
            characterStyle = "3D Pixar-like animated hero",
            avatarResId = R.drawable.demo_scene_1_1791295457145
        ),
        Character(
            id = "char_luna",
            name = "Luna",
            age = "Timeless",
            gender = "Female",
            appearance = "A friendly glowing celestial fox with soft translucent silver-blue fur and a radiant fluffy tail",
            hair = "Ethereal glowing fur with twinkling stardust particles",
            clothes = "Ancient carved crystal pendant resting gently against glowing chest fur",
            personality = "Gentle, wise, playfully guiding lost wanderers",
            voiceType = "Friendly voice",
            characterStyle = "3D Fantasy magical creature",
            avatarResId = R.drawable.demo_scene_3_1791295483544
        )
    )

    val demoScenes = listOf(
        Scene(
            id = "scene_1",
            sceneNumber = 1,
            durationSeconds = 6,
            location = "Whispering Twilight Forest",
            timeOfDay = "Night",
            characters = listOf("Ayan"),
            action = "Ayan steps cautiously into the enchanted forest at twilight, clutching his brass compass.",
            dialogue = "Ayan: 'The compass is spinning... something magical is out there.'",
            narration = "One quiet night, Ayan discovers a mysterious glowing path winding deep into the ancient forest.",
            visualPrompt = "Cinematic 3D animation style: Young explorer Ayan in warm jacket stepping through giant mossy trees at twilight, starry sky and bioluminescent mist, mystical purple-navy palette.",
            cameraMovement = "Slow dolly forward through trees",
            mood = "Mysterious & Wonder",
            soundEffect = "Night crickets, rustling twilight leaves, distant wind chime",
            imageResId = R.drawable.demo_scene_1_1791295457145
        ),
        Scene(
            id = "scene_2",
            sceneNumber = 2,
            durationSeconds = 6,
            location = "Deep Luminescent Grove",
            timeOfDay = "Night",
            characters = listOf("Ayan"),
            action = "The glowing footprints illuminate ahead on moss-covered roots as floating fireflies swirl.",
            dialogue = "Ayan: 'These glowing tracks... they aren't human footsteps!'",
            narration = "As he follows the luminous trail, the shadows of the woods give way to floating spheres of azure light.",
            visualPrompt = "Mysterious radiant turquoise glowing footprint trail illuminating mossy forest ground, giant ancient trees, floating blue fireflies, cinematic night lighting.",
            cameraMovement = "Low-angle tracking shot along glowing footprints",
            mood = "Enchanting & Intriguing",
            soundEffect = "Mystical shimmer chime, soft moss footsteps",
            imageResId = R.drawable.demo_scene_2_1791295663005
        ),
        Scene(
            id = "scene_3",
            sceneNumber = 3,
            durationSeconds = 6,
            location = "Sacred Willow Canopy",
            timeOfDay = "Night",
            characters = listOf("Ayan", "Luna"),
            action = "Luna the celestial fox steps out from behind shimmering leaves and tilts her head with a soft chime.",
            dialogue = "Luna: 'Do not be afraid, wanderer. You followed the call.'",
            narration = "From the emerald mist emerges Luna, a gentle celestial fox radiating warmth and silver-blue light.",
            visualPrompt = "Young explorer Ayan meeting Luna, a friendly glowing spirit fox with radiant blue fur and kind golden eyes under glowing weeping willow branches.",
            cameraMovement = "Medium eye-level two-shot with subtle floating orbit",
            mood = "Heartwarming & Magical",
            soundEffect = "Harmonic bell resonance, gentle fox breath",
            imageResId = R.drawable.demo_scene_3_1791295483544
        ),
        Scene(
            id = "scene_4",
            sceneNumber = 4,
            durationSeconds = 6,
            location = "Bioluminescent Valley",
            timeOfDay = "Midnight",
            characters = listOf("Ayan", "Luna"),
            action = "Ayan and Luna stroll together like old friends beside giant glowing blossoms blooming in the dark.",
            dialogue = "Ayan: 'Where are you taking me?' Luna: 'To where the moon rests.'",
            narration = "Together they journey side by side, fear melting into breathtaking discovery.",
            visualPrompt = "Young explorer boy and glowing blue fox walking together side by side through giant glowing flowers in shades of amethyst and turquoise under starry sky.",
            cameraMovement = "Side tracking shot moving smoothly with characters",
            mood = "Adventurous & Uplifting",
            soundEffect = "Blooming flower chime, rhythmic walking, night breeze",
            imageResId = R.drawable.demo_scene_4_1791295685589
        ),
        Scene(
            id = "scene_5",
            sceneNumber = 5,
            durationSeconds = 6,
            location = "The Hidden Moonlight Lake",
            timeOfDay = "Midnight",
            characters = listOf("Ayan", "Luna"),
            action = "They reach the cliff's edge overlooking a vast crystal lake mirroring the giant luminous moon.",
            dialogue = "Ayan: 'It's breathtaking... Luna, thank you for showing me this.'",
            narration = "Beneath the glowing celestial moonlight, they discover the hidden lake—a sanctuary of peace untouched by time.",
            visualPrompt = "Panoramic wide vista of breathtaking hidden crystal lake glowing with turquoise water under a giant radiant full moon, boy and glowing fox overlooking from cliff.",
            cameraMovement = "Grand cinematic crane shot pulling back to reveal the lake",
            mood = "Majestic Climax & Awe",
            soundEffect = "Epic orchestral swell, gentle water lapping, starlight chime",
            imageResId = R.drawable.demo_scene_5_1791295502224
        )
    )

    val demoProject = Project(
        id = "demo_moonlight_adventure",
        title = "The Moonlight Adventure",
        description = "One quiet night, Ayan discovers a mysterious glowing path in the forest. He follows it and meets Luna, a magical fox. Together they discover a hidden lake beneath the moonlight.",
        language = LanguageOption.English,
        durationLabel = "30 seconds",
        durationSeconds = 30,
        format = VideoFormat.Horizontal_16_9,
        visualStyle = VisualStyle.Cartoon_3D,
        voiceType = VoiceType.Storyteller,
        musicMood = MusicMood.Adventure,
        targetAudience = "Family & Kids",
        characters = demoCharacters,
        scenes = demoScenes,
        status = ProjectStatus.Ready,
        thumbnailResId = R.drawable.demo_scene_5_1791295502224
    )

    val templates = listOf(
        StoryTemplate(
            id = "tmpl_kids",
            title = "Kids Story",
            category = "Family & Children",
            description = "A heartwarming, colorful journey teaching curiosity, friendship, and courage.",
            visualStyle = VisualStyle.PixarFamily,
            musicMood = MusicMood.Happy,
            durationLabel = "30 seconds",
            format = VideoFormat.Horizontal_16_9,
            sampleStory = "A tiny squirrel named Sammy loses his golden acorn inside an oversized toy clock. With the help of Barnaby the clockwork bear, they solve whimsical gear puzzles and discover that working together makes any problem small.",
            iconName = "ChildCare",
            targetAudience = "Kids (Ages 4-10)"
        ),
        StoryTemplate(
            id = "tmpl_horror",
            title = "Horror Story",
            category = "Suspense & Thriller",
            description = "Chilling atmosphere, eerie shadows, and heart-pounding suspenseful reveals.",
            visualStyle = VisualStyle.Cinematic,
            musicMood = MusicMood.Suspense,
            durationLabel = "30 seconds",
            format = VideoFormat.Horizontal_16_9,
            sampleStory = "Rain lashes against the fogged windows of an abandoned Victorian lighthouse. When the keeper's daughter hears an ominous rhythm tapping behind the salt-crusted cellar door, the light atop the tower suddenly turns crimson.",
            iconName = "NightlightRound",
            targetAudience = "Teens & Adults"
        ),
        StoryTemplate(
            id = "tmpl_cartoon",
            title = "Funny Cartoon",
            category = "Humor & Comedy",
            description = "Bouncy comedy, slapstick gags, fast-paced laughter, and silly misunderstandings.",
            visualStyle = VisualStyle.Cartoon_2D,
            musicMood = MusicMood.Funny,
            durationLabel = "15 seconds",
            format = VideoFormat.Vertical_9_16,
            sampleStory = "Professor Pumpernickel invents an anti-gravity toaster that accidentally catapults pancakes into high orbit. Chaos ensues as pigeons with miniature astronaut helmets begin chasing flapjacks across the kitchen sky.",
            iconName = "Mood",
            targetAudience = "All Ages"
        ),
        StoryTemplate(
            id = "tmpl_adventure",
            title = "Adventure",
            category = "Action & Epic Quest",
            description = "Grand expeditions, ancient ruins, perilous cliffs, and legendary discoveries.",
            visualStyle = VisualStyle.Fantasy,
            musicMood = MusicMood.Adventure,
            durationLabel = "60 seconds",
            format = VideoFormat.Horizontal_16_9,
            sampleStory = "Deep in the Whispering Canyons, young cartographer Maya deciphers a map written in starlight. Braving shifting sandstorms and colossal stone sentinels, she reaches the Lost Sun Temple to reignite the ancient solar core.",
            iconName = "Explore",
            targetAudience = "General Audience"
        ),
        StoryTemplate(
            id = "tmpl_love",
            title = "Love Story",
            category = "Romance & Drama",
            description = "Tender glances, sweet coincidences, and heartwarming romantic serendipity.",
            visualStyle = VisualStyle.Anime,
            musicMood = MusicMood.Emotional,
            durationLabel = "30 seconds",
            format = VideoFormat.Vertical_9_16,
            sampleStory = "Under the cherry blossom rain of a quiet Kyoto train platform, two strangers continually swap identical vintage notebooks by mistake. Each day, their handwritten marginal notes grow into an unspoken bond of affection.",
            iconName = "Favorite",
            targetAudience = "Young Adults"
        ),
        StoryTemplate(
            id = "tmpl_moral",
            title = "Moral Story",
            category = "Inspirational",
            description = "Thoughtful fables with deep wisdom about honesty, humility, and generosity.",
            visualStyle = VisualStyle.Cartoon_3D,
            musicMood = MusicMood.Emotional,
            durationLabel = "30 seconds",
            format = VideoFormat.Horizontal_16_9,
            sampleStory = "An old potter in a sun-baked desert village gives away his most beautiful clay jars to carry water for thirsty travelers. When a sudden drought strikes, the grateful travelers return with overflowing springs to replenish his kiln.",
            iconName = "AutoAwesome",
            targetAudience = "Family & School"
        ),
        StoryTemplate(
            id = "tmpl_educational",
            title = "Educational Story",
            category = "Learning & Science",
            description = "Fun science adventures explaining outer space, oceans, biology, and history.",
            visualStyle = VisualStyle.Cartoon_3D,
            musicMood = MusicMood.Adventure,
            durationLabel = "30 seconds",
            format = VideoFormat.Horizontal_16_9,
            sampleStory = "Hop aboard the microscopic Nano-Submarine as Dr. Spark shrinks down to tour the human bloodstream, watching brave white blood cells defend the city against alien-like pollen invaders in real time.",
            iconName = "School",
            targetAudience = "Students & Curious Minds"
        ),
        StoryTemplate(
            id = "tmpl_shortfilm",
            title = "Short Film",
            category = "Cinematic Narrative",
            description = "Deep emotional storytelling with dramatic cinematography and auteur lighting.",
            visualStyle = VisualStyle.Cinematic,
            musicMood = MusicMood.Cinematic,
            durationLabel = "60 seconds",
            format = VideoFormat.Horizontal_16_9,
            sampleStory = "An aging jazz saxophonist in 1950s Chicago plays a final midnight ballad alone in the subway station. As the notes echo, reflections in passing train windows reveal moments of his youth, love, and redemption.",
            iconName = "Movie",
            targetAudience = "Film Lovers"
        ),
        StoryTemplate(
            id = "tmpl_yt_short",
            title = "YouTube Short",
            category = "High Retention Vertical",
            description = "Punchy hook in first 2 seconds, rapid pacing, eye-catching visual cuts.",
            visualStyle = VisualStyle.Anime,
            musicMood = MusicMood.Happy,
            durationLabel = "15 seconds",
            format = VideoFormat.Vertical_9_16,
            sampleStory = "Did you know that ravens can remember human faces for five years? Meet Corvo, the hyper-intelligent raven who trained his human neighbor to trade shiny quarters for fresh blueberries every Tuesday morning!",
            iconName = "OndemandVideo",
            targetAudience = "Social Media Viewers"
        ),
        StoryTemplate(
            id = "tmpl_tiktok",
            title = "TikTok / Reels",
            category = "Viral Trending",
            description = "Fast-cut pacing, dynamic subtitles, instant curiosity gap, and relatable twist.",
            visualStyle = VisualStyle.Cartoon_3D,
            musicMood = MusicMood.Funny,
            durationLabel = "15 seconds",
            format = VideoFormat.Vertical_9_16,
            sampleStory = "POV: You think you're adopting a normal black cat, but at exactly 3:14 AM every night he summons a council of neighborhood raccoons to review PowerPoint slides on trashcan optimization.",
            iconName = "Smartphone",
            targetAudience = "Reels & TikTok Feed"
        )
    )
}
