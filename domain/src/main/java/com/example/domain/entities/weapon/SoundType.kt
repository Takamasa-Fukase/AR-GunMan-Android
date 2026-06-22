package com.example.domain.entities.weapon

enum class SoundType(
    val rawValue: String
) {
    WESTERN_PISTOL_FIRE("western_pistol_fire"),
    PISTOL_APPEAR("pistol_appear"),
    PISTOL_FIRE("pistol_fire"),
    PISTOL_OUT_OF_BULLETS("pistol_out_of_bullets"),
    PISTOL_RELOAD("pistol_reload"),
    BAZOOKA_APPEAR("bazooka_appear"),
    BAZOOKA_FIRE("bazooka_fire"),
    BAZOOKA_RELOAD("bazooka_reload"),
    BAZOOKA_EXPLOSION("bazooka_explosion"),
    TARGET_HIT("target_hit"),
    TARGET_APPEARANCE_CHANGE("target_appearance_change"),
    START_WHISTLE("start_whistle"),
    END_WHISTLE("end_whistle"),
    RANKING_APPEAR("ranking_appear");

    val needsPlayVibration: Boolean
        get() {
            return this == PISTOL_FIRE || this == BAZOOKA_FIRE
        }
}