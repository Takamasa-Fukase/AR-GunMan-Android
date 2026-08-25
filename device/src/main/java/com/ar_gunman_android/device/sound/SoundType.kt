package com.ar_gunman_android.device.sound

import com.ar_gunman_android.device.R

enum class SoundType(
    val resId: Int,
) {
    WESTERN_PISTOL_FIRE(R.raw.western_pistol_fire),
    PISTOL_APPEAR(R.raw.pistol_appear),
    PISTOL_FIRE(R.raw.pistol_fire),
    PISTOL_OUT_OF_BULLETS(R.raw.pistol_out_of_bullets),
    PISTOL_RELOAD(R.raw.pistol_reload),
    BAZOOKA_APPEAR(R.raw.bazooka_appear),
    BAZOOKA_FIRE(R.raw.bazooka_fire),
    BAZOOKA_RELOAD(R.raw.bazooka_reload),
    BAZOOKA_EXPLOSION(R.raw.bazooka_explosion),
    TARGET_HIT(R.raw.target_hit),
    TARGET_APPEARANCE_CHANGE(R.raw.target_appearance_change),
    START_WHISTLE(R.raw.start_whistle),
    END_WHISTLE(R.raw.end_whistle),
    RANKING_APPEAR(R.raw.ranking_appear);

    val needsPlayVibration: Boolean
        get() {
            return this == PISTOL_FIRE || this == BAZOOKA_FIRE
        }
}