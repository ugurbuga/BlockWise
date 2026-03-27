package com.ugurbuga.blockwise

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import org.jetbrains.compose.resources.StringResource

import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
import blockwise.composeapp.generated.resources.board_block_style
import blockwise.composeapp.generated.resources.board_block_style_flat
import blockwise.composeapp.generated.resources.board_block_style_match_selected
import blockwise.composeapp.generated.resources.block_color_palette
import blockwise.composeapp.generated.resources.block_palette_candy
import blockwise.composeapp.generated.resources.block_palette_classic
import blockwise.composeapp.generated.resources.block_palette_earth
import blockwise.composeapp.generated.resources.block_palette_neon
import blockwise.composeapp.generated.resources.block_style
import blockwise.composeapp.generated.resources.block_style_bubble
import blockwise.composeapp.generated.resources.block_style_flat
import blockwise.composeapp.generated.resources.block_style_outline
import blockwise.composeapp.generated.resources.block_style_liquid_glass
import blockwise.composeapp.generated.resources.block_style_neon
import blockwise.composeapp.generated.resources.block_style_sharp_3d
import blockwise.composeapp.generated.resources.block_style_wood
import blockwise.composeapp.generated.resources.block_gap_spacing
import blockwise.composeapp.generated.resources.block_gap_spacing_none
import blockwise.composeapp.generated.resources.block_gap_spacing_low
import blockwise.composeapp.generated.resources.block_gap_spacing_high
import blockwise.composeapp.generated.resources.color_palette
import blockwise.composeapp.generated.resources.color_palette_aurora
import blockwise.composeapp.generated.resources.color_palette_classic
import blockwise.composeapp.generated.resources.color_palette_sunset
import blockwise.composeapp.generated.resources.difficulty
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.difficulty_very_hard
import blockwise.composeapp.generated.resources.drag_finger_offset
import blockwise.composeapp.generated.resources.drag_finger_offset_high
import blockwise.composeapp.generated.resources.drag_finger_offset_low
import blockwise.composeapp.generated.resources.drag_finger_offset_medium
import blockwise.composeapp.generated.resources.drag_finger_offset_none
import blockwise.composeapp.generated.resources.game_over
import blockwise.composeapp.generated.resources.game_over_message
import blockwise.composeapp.generated.resources.grid_size
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.invalid_placement
import blockwise.composeapp.generated.resources.invalid_placement_adjacent_col
import blockwise.composeapp.generated.resources.invalid_placement_adjacent_row
import blockwise.composeapp.generated.resources.invalid_placement_distinct_col
import blockwise.composeapp.generated.resources.invalid_placement_distinct_row
import blockwise.composeapp.generated.resources.invalid_placement_feedback_mode
import blockwise.composeapp.generated.resources.invalid_placement_feedback_mode_on_drop
import blockwise.composeapp.generated.resources.invalid_placement_feedback_mode_while_dragging
import blockwise.composeapp.generated.resources.invalid_placement_out_of_bounds
import blockwise.composeapp.generated.resources.invalid_placement_overlap
import blockwise.composeapp.generated.resources.language
import blockwise.composeapp.generated.resources.language_arabic
import blockwise.composeapp.generated.resources.language_english
import blockwise.composeapp.generated.resources.language_french
import blockwise.composeapp.generated.resources.language_german
import blockwise.composeapp.generated.resources.language_russian
import blockwise.composeapp.generated.resources.language_spanish
import blockwise.composeapp.generated.resources.language_turkish
import blockwise.composeapp.generated.resources.level_selection_title
import blockwise.composeapp.generated.resources.menu
import blockwise.composeapp.generated.resources.moves_remaining
import blockwise.composeapp.generated.resources.neon_pulse_speed
import blockwise.composeapp.generated.resources.neon_pulse_speed_fast
import blockwise.composeapp.generated.resources.neon_pulse_speed_normal
import blockwise.composeapp.generated.resources.neon_pulse_speed_slow
import blockwise.composeapp.generated.resources.new_game
import blockwise.composeapp.generated.resources.ok
import blockwise.composeapp.generated.resources.play
import blockwise.composeapp.generated.resources.rule_color_limit_row
import blockwise.composeapp.generated.resources.rule_color_limit_col
import blockwise.composeapp.generated.resources.rules
import blockwise.composeapp.generated.resources.rules_adjacent_limit_desc_disabled
import blockwise.composeapp.generated.resources.rules_adjacent_limit_desc_enabled
import blockwise.composeapp.generated.resources.rules_adjacent_limit_title
import blockwise.composeapp.generated.resources.rules_current_mode
import blockwise.composeapp.generated.resources.rules_intro
import blockwise.composeapp.generated.resources.rules_move_limit_desc_disabled
import blockwise.composeapp.generated.resources.rules_move_limit_desc_enabled
import blockwise.composeapp.generated.resources.rules_move_limit_title
import blockwise.composeapp.generated.resources.rules_piece_pool_desc
import blockwise.composeapp.generated.resources.rules_piece_pool_title
import blockwise.composeapp.generated.resources.rules_prefilled_desc
import blockwise.composeapp.generated.resources.rules_prefilled_title
import blockwise.composeapp.generated.resources.rules_rule_1_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_1_desc_enabled
import blockwise.composeapp.generated.resources.rules_rule_1_title
import blockwise.composeapp.generated.resources.rules_rule_2_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_2_desc_enabled
import blockwise.composeapp.generated.resources.rules_rule_2_title
import blockwise.composeapp.generated.resources.rules_tips_desc
import blockwise.composeapp.generated.resources.rules_tips_title
import blockwise.composeapp.generated.resources.rules_title
import blockwise.composeapp.generated.resources.rules_variety_desc_disabled
import blockwise.composeapp.generated.resources.rules_variety_desc_enabled
import blockwise.composeapp.generated.resources.rules_variety_title
import blockwise.composeapp.generated.resources.score
import blockwise.composeapp.generated.resources.scores
import blockwise.composeapp.generated.resources.scores_best_for_mode
import blockwise.composeapp.generated.resources.scores_empty
import blockwise.composeapp.generated.resources.settings
import blockwise.composeapp.generated.resources.scores_title
import blockwise.composeapp.generated.resources.select_piece
import blockwise.composeapp.generated.resources.selected_mode_best_score
import blockwise.composeapp.generated.resources.shapes_preview_section_2x2
import blockwise.composeapp.generated.resources.shapes_preview_section_3x3
import blockwise.composeapp.generated.resources.shapes_preview_section_4x4
import blockwise.composeapp.generated.resources.shapes_preview_section_5x5
import blockwise.composeapp.generated.resources.shapes_preview_title
import blockwise.composeapp.generated.resources.theme
import blockwise.composeapp.generated.resources.theme_dark
import blockwise.composeapp.generated.resources.theme_light
import blockwise.composeapp.generated.resources.theme_system

internal val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.English }

@Composable
internal fun localizedStringResource(resource: StringResource): String {
    return localizedGetString(LocalAppLanguage.current, resource)
}

@Composable
internal fun localizedStringResource(resource: StringResource, vararg formatArgs: Any): String {
    return localizedGetString(LocalAppLanguage.current, resource, *formatArgs)
}

internal fun localizedGetString(language: AppLanguage, resource: StringResource, vararg formatArgs: Any): String {
    fun intArg(index: Int): Int = (formatArgs[index] as Number).toInt()
    fun textArg(index: Int): String = formatArgs[index].toString()
    fun gridSizeOptionValue(size: Int): String = "${size}x${size}"

    return when (resource) {
        Res.string.score -> when (language) {
            AppLanguage.English -> "Score: ${intArg(0)}"
            AppLanguage.Turkish -> "Skor: ${intArg(0)}"
            AppLanguage.Spanish -> "Puntuación: ${intArg(0)}"
            AppLanguage.French -> "Score : ${intArg(0)}"
            AppLanguage.German -> "Punktestand: ${intArg(0)}"
            AppLanguage.Russian -> "Счёт: ${intArg(0)}"
            AppLanguage.Arabic -> "النتيجة: ${intArg(0)}"
        }

        Res.string.grid_size -> when (language) {
            AppLanguage.English -> "Grid Size"
            AppLanguage.Turkish -> "Izgara Boyutu"
            AppLanguage.Spanish -> "Tamaño de cuadrícula"
            AppLanguage.French -> "Taille de la grille"
            AppLanguage.German -> "Rastergröße"
            AppLanguage.Russian -> "Размер сетки"
            AppLanguage.Arabic -> "حجم الشبكة"
        }

        Res.string.grid_size_option -> gridSizeOptionValue(intArg(0))

        Res.string.language -> when (language) {
            AppLanguage.English -> "Language"
            AppLanguage.Turkish -> "Dil"
            AppLanguage.Spanish -> "Idioma"
            AppLanguage.French -> "Langue"
            AppLanguage.German -> "Sprache"
            AppLanguage.Russian -> "Язык"
            AppLanguage.Arabic -> "اللغة"
        }

        Res.string.settings -> when (language) {
            AppLanguage.English -> "Settings"
            AppLanguage.Turkish -> "Ayarlar"
            AppLanguage.Spanish -> "Ajustes"
            AppLanguage.French -> "Réglages"
            AppLanguage.German -> "Einstellungen"
            AppLanguage.Russian -> "Настройки"
            AppLanguage.Arabic -> "الإعدادات"
        }

        Res.string.difficulty -> when (language) {
            AppLanguage.English -> "Difficulty"
            AppLanguage.Turkish -> "Zorluk"
            AppLanguage.Spanish -> "Dificultad"
            AppLanguage.French -> "Difficulté"
            AppLanguage.German -> "Schwierigkeit"
            AppLanguage.Russian -> "Сложность"
            AppLanguage.Arabic -> "مستوى الصعوبة"
        }

        Res.string.theme -> when (language) {
            AppLanguage.English -> "Theme"
            AppLanguage.Turkish -> "Tema"
            AppLanguage.Spanish -> "Tema"
            AppLanguage.French -> "Thème"
            AppLanguage.German -> "Design"
            AppLanguage.Russian -> "Тема"
            AppLanguage.Arabic -> "المظهر"
        }

        Res.string.theme_system -> when (language) {
            AppLanguage.English -> "System"
            AppLanguage.Turkish -> "Sistem"
            AppLanguage.Spanish -> "Sistema"
            AppLanguage.French -> "Système"
            AppLanguage.German -> "System"
            AppLanguage.Russian -> "Система"
            AppLanguage.Arabic -> "النظام"
        }

        Res.string.theme_light -> when (language) {
            AppLanguage.English -> "Light"
            AppLanguage.Turkish -> "Açık"
            AppLanguage.Spanish -> "Claro"
            AppLanguage.French -> "Clair"
            AppLanguage.German -> "Hell"
            AppLanguage.Russian -> "Светлая"
            AppLanguage.Arabic -> "فاتح"
        }

        Res.string.theme_dark -> when (language) {
            AppLanguage.English -> "Dark"
            AppLanguage.Turkish -> "Koyu"
            AppLanguage.Spanish -> "Oscuro"
            AppLanguage.French -> "Sombre"
            AppLanguage.German -> "Dunkel"
            AppLanguage.Russian -> "Тёмная"
            AppLanguage.Arabic -> "داكن"
        }

        Res.string.color_palette -> when (language) {
            AppLanguage.English -> "Theme Color Palette"
            AppLanguage.Turkish -> "Tema Renk Paleti"
            AppLanguage.Spanish -> "Paleta de color del tema"
            AppLanguage.French -> "Palette de couleurs du thème"
            AppLanguage.German -> "Themen-Farbpalette"
            AppLanguage.Russian -> "Палитра темы"
            AppLanguage.Arabic -> "لوحة ألوان السمة"
        }

        Res.string.color_palette_classic -> when (language) {
            AppLanguage.English -> "Classic"
            AppLanguage.Turkish -> "Klasik"
            AppLanguage.Spanish -> "Clásica"
            AppLanguage.French -> "Classique"
            AppLanguage.German -> "Klassisch"
            AppLanguage.Russian -> "Классика"
            AppLanguage.Arabic -> "كلاسيكي"
        }

        Res.string.color_palette_aurora -> when (language) {
            AppLanguage.English -> "Aurora"
            AppLanguage.Turkish -> "Aurora"
            AppLanguage.Spanish -> "Aurora"
            AppLanguage.French -> "Aurore"
            AppLanguage.German -> "Aurora"
            AppLanguage.Russian -> "Аврора"
            AppLanguage.Arabic -> "أورورا"
        }

        Res.string.color_palette_sunset -> when (language) {
            AppLanguage.English -> "Sunset"
            AppLanguage.Turkish -> "Gün Batımı"
            AppLanguage.Spanish -> "Atardecer"
            AppLanguage.French -> "Coucher de soleil"
            AppLanguage.German -> "Sonnenuntergang"
            AppLanguage.Russian -> "Закат"
            AppLanguage.Arabic -> "غروب"
        }

        Res.string.block_color_palette -> when (language) {
            AppLanguage.English -> "Block Color Palette"
            AppLanguage.Turkish -> "Blok Renk Paleti"
            AppLanguage.Spanish -> "Paleta de color de bloques"
            AppLanguage.French -> "Palette de couleurs des blocs"
            AppLanguage.German -> "Block-Farbpalette"
            AppLanguage.Russian -> "Палитра блоков"
            AppLanguage.Arabic -> "لوحة ألوان الكتل"
        }

        Res.string.block_palette_classic -> when (language) {
            AppLanguage.English -> "Classic"
            AppLanguage.Turkish -> "Klasik"
            AppLanguage.Spanish -> "Clásica"
            AppLanguage.French -> "Classique"
            AppLanguage.German -> "Klassisch"
            AppLanguage.Russian -> "Классика"
            AppLanguage.Arabic -> "كلاسيكي"
        }

        Res.string.block_palette_candy -> when (language) {
            AppLanguage.English -> "Candy"
            AppLanguage.Turkish -> "Şeker"
            AppLanguage.Spanish -> "Caramelo"
            AppLanguage.French -> "Bonbon"
            AppLanguage.German -> "Candy"
            AppLanguage.Russian -> "Конфетная"
            AppLanguage.Arabic -> "حلويات"
        }

        Res.string.block_palette_neon -> when (language) {
            AppLanguage.English -> "Neon"
            AppLanguage.Turkish -> "Neon"
            AppLanguage.Spanish -> "Neón"
            AppLanguage.French -> "Néon"
            AppLanguage.German -> "Neon"
            AppLanguage.Russian -> "Неон"
            AppLanguage.Arabic -> "نيون"
        }

        Res.string.block_palette_earth -> when (language) {
            AppLanguage.English -> "Earth"
            AppLanguage.Turkish -> "Toprak"
            AppLanguage.Spanish -> "Tierra"
            AppLanguage.French -> "Terre"
            AppLanguage.German -> "Erde"
            AppLanguage.Russian -> "Земля"
            AppLanguage.Arabic -> "ترابي"
        }

        Res.string.block_style -> when (language) {
            AppLanguage.English -> "Block Style"
            AppLanguage.Turkish -> "Blok Stili"
            AppLanguage.Spanish -> "Estilo de bloque"
            AppLanguage.French -> "Style des blocs"
            AppLanguage.German -> "Blockstil"
            AppLanguage.Russian -> "Стиль блоков"
            AppLanguage.Arabic -> "نمط الكتل"
        }

        Res.string.block_style_flat -> when (language) {
            AppLanguage.English -> "Flat"
            AppLanguage.Turkish -> "Düz"
            AppLanguage.Spanish -> "Plano"
            AppLanguage.French -> "Plat"
            AppLanguage.German -> "Flach"
            AppLanguage.Russian -> "Плоский"
            AppLanguage.Arabic -> "مسطّح"
        }

        Res.string.block_style_sharp_3d -> when (language) {
            AppLanguage.English -> "Sharp 3D"
            AppLanguage.Turkish -> "Köşeli 3D"
            AppLanguage.Spanish -> "3D angular"
            AppLanguage.French -> "3D anguleux"
            AppLanguage.German -> "Kantiges 3D"
            AppLanguage.Russian -> "Угловатый 3D"
            AppLanguage.Arabic -> "ثلاثي الأبعاد الحاد"
        }

        Res.string.block_style_liquid_glass -> when (language) {
            AppLanguage.English -> "Liquid Glass"
            AppLanguage.Turkish -> "Liquid Glass"
            AppLanguage.Spanish -> "Vidrio líquido"
            AppLanguage.French -> "Verre liquide"
            AppLanguage.German -> "Liquid Glass"
            AppLanguage.Russian -> "Жидкое стекло"
            AppLanguage.Arabic -> "زجاج سائل"
        }

        Res.string.block_style_neon -> when (language) {
            AppLanguage.English -> "Neon"
            AppLanguage.Turkish -> "Neon"
            AppLanguage.Spanish -> "Neón"
            AppLanguage.French -> "Néon"
            AppLanguage.German -> "Neon"
            AppLanguage.Russian -> "Неон"
            AppLanguage.Arabic -> "نيون"
        }

        Res.string.board_block_style -> when (language) {
            AppLanguage.English -> "Board Block Style"
            AppLanguage.Turkish -> "Board Blok Stili"
            AppLanguage.Spanish -> "Estilo de bloque del tablero"
            AppLanguage.French -> "Style des blocs du plateau"
            AppLanguage.German -> "Blockstil auf dem Brett"
            AppLanguage.Russian -> "Стиль блоков на поле"
            AppLanguage.Arabic -> "نمط كتل اللوحة"
        }

        Res.string.board_block_style_flat -> when (language) {
            AppLanguage.English -> "Always Flat"
            AppLanguage.Turkish -> "Her zaman düz"
            AppLanguage.Spanish -> "Siempre plano"
            AppLanguage.French -> "Toujours plat"
            AppLanguage.German -> "Immer flach"
            AppLanguage.Russian -> "Всегда плоский"
            AppLanguage.Arabic -> "مسطّح دائمًا"
        }

        Res.string.board_block_style_match_selected -> when (language) {
            AppLanguage.English -> "Match selected block style"
            AppLanguage.Turkish -> "Seçili blok stilini eşleştir"
            AppLanguage.Spanish -> "Usar el estilo de bloque seleccionado"
            AppLanguage.French -> "Suivre le style de bloc sélectionné"
            AppLanguage.German -> "Ausgewählten Blockstil übernehmen"
            AppLanguage.Russian -> "Повторять выбранный стиль блока"
            AppLanguage.Arabic -> "مطابقة نمط الكتلة المحدد"
        }

        Res.string.block_gap_spacing -> when (language) {
            AppLanguage.English -> "Block Gap"
            AppLanguage.Turkish -> "Blok Aralığı"
            AppLanguage.Spanish -> "Separación entre bloques"
            AppLanguage.French -> "Espacement des blocs"
            AppLanguage.German -> "Blockabstand"
            AppLanguage.Russian -> "Зазор между блоками"
            AppLanguage.Arabic -> "المسافة بين الكتل"
        }

        Res.string.block_gap_spacing_none -> when (language) {
            AppLanguage.English -> "None"
            AppLanguage.Turkish -> "Boşluksuz"
            AppLanguage.Spanish -> "Sin espacio"
            AppLanguage.French -> "Sans espace"
            AppLanguage.German -> "Ohne Abstand"
            AppLanguage.Russian -> "Без зазора"
            AppLanguage.Arabic -> "بدون فراغ"
        }

        Res.string.block_gap_spacing_low -> when (language) {
            AppLanguage.English -> "Low"
            AppLanguage.Turkish -> "Az"
            AppLanguage.Spanish -> "Bajo"
            AppLanguage.French -> "Faible"
            AppLanguage.German -> "Wenig"
            AppLanguage.Russian -> "Малый"
            AppLanguage.Arabic -> "قليل"
        }

        Res.string.block_gap_spacing_high -> when (language) {
            AppLanguage.English -> "High"
            AppLanguage.Turkish -> "Çok"
            AppLanguage.Spanish -> "Alto"
            AppLanguage.French -> "Élevé"
            AppLanguage.German -> "Groß"
            AppLanguage.Russian -> "Большой"
            AppLanguage.Arabic -> "كبير"
        }

        Res.string.neon_pulse_speed -> when (language) {
            AppLanguage.English -> "Neon Pulse Speed"
            AppLanguage.Turkish -> "Neon Nabız Hızı"
            AppLanguage.Spanish -> "Velocidad del pulso neón"
            AppLanguage.French -> "Vitesse du pulse néon"
            AppLanguage.German -> "Neon-Pulsgeschwindigkeit"
            AppLanguage.Russian -> "Скорость неонового пульса"
            AppLanguage.Arabic -> "سرعة نبض النيون"
        }

        Res.string.neon_pulse_speed_slow -> when (language) {
            AppLanguage.English -> "Slow"
            AppLanguage.Turkish -> "Yavaş"
            AppLanguage.Spanish -> "Lento"
            AppLanguage.French -> "Lent"
            AppLanguage.German -> "Langsam"
            AppLanguage.Russian -> "Медленно"
            AppLanguage.Arabic -> "بطيء"
        }

        Res.string.neon_pulse_speed_normal -> when (language) {
            AppLanguage.English -> "Normal"
            AppLanguage.Turkish -> "Normal"
            AppLanguage.Spanish -> "Normal"
            AppLanguage.French -> "Normal"
            AppLanguage.German -> "Normal"
            AppLanguage.Russian -> "Нормально"
            AppLanguage.Arabic -> "عادي"
        }

        Res.string.neon_pulse_speed_fast -> when (language) {
            AppLanguage.English -> "Fast"
            AppLanguage.Turkish -> "Hızlı"
            AppLanguage.Spanish -> "Rápido"
            AppLanguage.French -> "Rapide"
            AppLanguage.German -> "Schnell"
            AppLanguage.Russian -> "Быстро"
            AppLanguage.Arabic -> "سريع"
        }

        Res.string.drag_finger_offset -> when (language) {
            AppLanguage.English -> "Finger Offset"
            AppLanguage.Turkish -> "Parmak Ofseti"
            AppLanguage.Spanish -> "Desplazamiento del dedo"
            AppLanguage.French -> "Décalage du doigt"
            AppLanguage.German -> "Fingerabstand"
            AppLanguage.Russian -> "Смещение пальца"
            AppLanguage.Arabic -> "إزاحة الإصبع"
        }

        Res.string.drag_finger_offset_none -> when (language) {
            AppLanguage.English -> "None"
            AppLanguage.Turkish -> "Yok"
            AppLanguage.Spanish -> "Ninguno"
            AppLanguage.French -> "Aucun"
            AppLanguage.German -> "Keiner"
            AppLanguage.Russian -> "Нет"
            AppLanguage.Arabic -> "بدون"
        }

        Res.string.drag_finger_offset_low -> when (language) {
            AppLanguage.English -> "Low"
            AppLanguage.Turkish -> "Az"
            AppLanguage.Spanish -> "Bajo"
            AppLanguage.French -> "Faible"
            AppLanguage.German -> "Wenig"
            AppLanguage.Russian -> "Мало"
            AppLanguage.Arabic -> "قليل"
        }

        Res.string.drag_finger_offset_medium -> when (language) {
            AppLanguage.English -> "Medium"
            AppLanguage.Turkish -> "Orta"
            AppLanguage.Spanish -> "Medio"
            AppLanguage.French -> "Moyen"
            AppLanguage.German -> "Mittel"
            AppLanguage.Russian -> "Средне"
            AppLanguage.Arabic -> "متوسط"
        }

        Res.string.drag_finger_offset_high -> when (language) {
            AppLanguage.English -> "High"
            AppLanguage.Turkish -> "Çok"
            AppLanguage.Spanish -> "Alto"
            AppLanguage.French -> "Élevé"
            AppLanguage.German -> "Stark"
            AppLanguage.Russian -> "Сильно"
            AppLanguage.Arabic -> "كبير"
        }

        Res.string.invalid_placement_feedback_mode -> when (language) {
            AppLanguage.English -> "Error Messages"
            AppLanguage.Turkish -> "Hata Mesajları"
            AppLanguage.Spanish -> "Mensajes de error"
            AppLanguage.French -> "Messages d’erreur"
            AppLanguage.German -> "Fehlermeldungen"
            AppLanguage.Russian -> "Сообщения об ошибках"
            AppLanguage.Arabic -> "رسائل الخطأ"
        }

        Res.string.invalid_placement_feedback_mode_while_dragging -> when (language) {
            AppLanguage.English -> "While dragging"
            AppLanguage.Turkish -> "Parça hareket halindeyken"
            AppLanguage.Spanish -> "Mientras arrastras"
            AppLanguage.French -> "Pendant le glisser"
            AppLanguage.German -> "Beim Ziehen"
            AppLanguage.Russian -> "Во время перетаскивания"
            AppLanguage.Arabic -> "أثناء السحب"
        }

        Res.string.invalid_placement_feedback_mode_on_drop -> when (language) {
            AppLanguage.English -> "On drop"
            AppLanguage.Turkish -> "Parçayı bıraktığında"
            AppLanguage.Spanish -> "Al soltar"
            AppLanguage.French -> "Au relâchement"
            AppLanguage.German -> "Beim Ablegen"
            AppLanguage.Russian -> "При отпускании"
            AppLanguage.Arabic -> "عند الإفلات"
        }

        Res.string.language_english -> when (language) {
            AppLanguage.English -> "English"
            AppLanguage.Turkish -> "İngilizce"
            AppLanguage.Spanish -> "Inglés"
            AppLanguage.French -> "Anglais"
            AppLanguage.German -> "Englisch"
            AppLanguage.Russian -> "Английский"
            AppLanguage.Arabic -> "الإنجليزية"
        }

        Res.string.language_turkish -> when (language) {
            AppLanguage.English -> "Turkish"
            AppLanguage.Turkish -> "Türkçe"
            AppLanguage.Spanish -> "Turco"
            AppLanguage.French -> "Turc"
            AppLanguage.German -> "Türkisch"
            AppLanguage.Russian -> "Турецкий"
            AppLanguage.Arabic -> "التركية"
        }

        Res.string.language_spanish -> when (language) {
            AppLanguage.English -> "Spanish"
            AppLanguage.Turkish -> "İspanyolca"
            AppLanguage.Spanish -> "Español"
            AppLanguage.French -> "Espagnol"
            AppLanguage.German -> "Spanisch"
            AppLanguage.Russian -> "Испанский"
            AppLanguage.Arabic -> "الإسبانية"
        }

        Res.string.language_french -> when (language) {
            AppLanguage.English -> "French"
            AppLanguage.Turkish -> "Fransızca"
            AppLanguage.Spanish -> "Francés"
            AppLanguage.French -> "Français"
            AppLanguage.German -> "Französisch"
            AppLanguage.Russian -> "Французский"
            AppLanguage.Arabic -> "الفرنسية"
        }

        Res.string.language_german -> when (language) {
            AppLanguage.English -> "German"
            AppLanguage.Turkish -> "Almanca"
            AppLanguage.Spanish -> "Alemán"
            AppLanguage.French -> "Allemand"
            AppLanguage.German -> "Deutsch"
            AppLanguage.Russian -> "Немецкий"
            AppLanguage.Arabic -> "الألمانية"
        }

        Res.string.language_russian -> when (language) {
            AppLanguage.English -> "Russian"
            AppLanguage.Turkish -> "Rusça"
            AppLanguage.Spanish -> "Ruso"
            AppLanguage.French -> "Russe"
            AppLanguage.German -> "Russisch"
            AppLanguage.Russian -> "Русский"
            AppLanguage.Arabic -> "الروسية"
        }

        Res.string.language_arabic -> when (language) {
            AppLanguage.English -> "Arabic"
            AppLanguage.Turkish -> "Arapça"
            AppLanguage.Spanish -> "Árabe"
            AppLanguage.French -> "Arabe"
            AppLanguage.German -> "Arabisch"
            AppLanguage.Russian -> "Арабский"
            AppLanguage.Arabic -> "العربية"
        }

        Res.string.difficulty_easy -> when (language) {
            AppLanguage.English -> "Easy"
            AppLanguage.Turkish -> "Kolay"
            AppLanguage.Spanish -> "Fácil"
            AppLanguage.French -> "Facile"
            AppLanguage.German -> "Leicht"
            AppLanguage.Russian -> "Лёгкий"
            AppLanguage.Arabic -> "سهل"
        }

        Res.string.difficulty_normal -> when (language) {
            AppLanguage.English -> "Normal"
            AppLanguage.Turkish -> "Normal"
            AppLanguage.Spanish -> "Normal"
            AppLanguage.French -> "Normal"
            AppLanguage.German -> "Normal"
            AppLanguage.Russian -> "Обычный"
            AppLanguage.Arabic -> "عادي"
        }

        Res.string.difficulty_hard -> when (language) {
            AppLanguage.English -> "Hard"
            AppLanguage.Turkish -> "Zor"
            AppLanguage.Spanish -> "Difícil"
            AppLanguage.French -> "Difficile"
            AppLanguage.German -> "Schwer"
            AppLanguage.Russian -> "Сложный"
            AppLanguage.Arabic -> "صعب"
        }

        Res.string.difficulty_very_hard -> when (language) {
            AppLanguage.English -> "Expert"
            AppLanguage.Turkish -> "Uzman"
            AppLanguage.Spanish -> "Experto"
            AppLanguage.French -> "Expert"
            AppLanguage.German -> "Experte"
            AppLanguage.Russian -> "Эксперт"
            AppLanguage.Arabic -> "خبير"
        }

        Res.string.rules -> when (language) {
            AppLanguage.English -> "Rules"
            AppLanguage.Turkish -> "Kurallar"
            AppLanguage.Spanish -> "Reglas"
            AppLanguage.French -> "Règles"
            AppLanguage.German -> "Regeln"
            AppLanguage.Russian -> "Правила"
            AppLanguage.Arabic -> "القواعد"
        }

        Res.string.scores -> when (language) {
            AppLanguage.English -> "Scores"
            AppLanguage.Turkish -> "Puanlar"
            AppLanguage.Spanish -> "Puntuaciones"
            AppLanguage.French -> "Scores"
            AppLanguage.German -> "Punkte"
            AppLanguage.Russian -> "Рекорды"
            AppLanguage.Arabic -> "النتائج"
        }

        Res.string.back -> when (language) {
            AppLanguage.English -> "Back"
            AppLanguage.Turkish -> "Geri"
            AppLanguage.Spanish -> "Atrás"
            AppLanguage.French -> "Retour"
            AppLanguage.German -> "Zurück"
            AppLanguage.Russian -> "Назад"
            AppLanguage.Arabic -> "رجوع"
        }

        Res.string.menu -> when (language) {
            AppLanguage.English -> "Menu"
            AppLanguage.Turkish -> "Menü"
            AppLanguage.Spanish -> "Menú"
            AppLanguage.French -> "Menu"
            AppLanguage.German -> "Menü"
            AppLanguage.Russian -> "Меню"
            AppLanguage.Arabic -> "القائمة"
        }

        Res.string.play -> when (language) {
            AppLanguage.English -> "Play"
            AppLanguage.Turkish -> "Oyna"
            AppLanguage.Spanish -> "Jugar"
            AppLanguage.French -> "Jouer"
            AppLanguage.German -> "Spielen"
            AppLanguage.Russian -> "Играть"
            AppLanguage.Arabic -> "ابدأ"
        }

        Res.string.level_selection_title -> when (language) {
            AppLanguage.English -> "Level Selection"
            AppLanguage.Turkish -> "Seviye Seçimi"
            AppLanguage.Spanish -> "Selección de nivel"
            AppLanguage.French -> "Sélection du niveau"
            AppLanguage.German -> "Levelauswahl"
            AppLanguage.Russian -> "Выбор уровня"
            AppLanguage.Arabic -> "اختيار المستوى"
        }

        Res.string.shapes_preview_title -> when (language) {
            AppLanguage.English -> "Shapes"
            AppLanguage.Turkish -> "Şekiller"
            AppLanguage.Spanish,
            AppLanguage.French,
            AppLanguage.German,
            AppLanguage.Russian,
            AppLanguage.Arabic,
            -> "Shapes"
        }

        Res.string.shapes_preview_section_2x2 -> "2x2"
        Res.string.shapes_preview_section_3x3 -> "3x3"
        Res.string.shapes_preview_section_4x4 -> "4x4"
        Res.string.shapes_preview_section_5x5 -> "5x5"

        Res.string.new_game -> when (language) {
            AppLanguage.English -> "New Game"
            AppLanguage.Turkish -> "Yeni Oyun"
            AppLanguage.Spanish -> "Nueva partida"
            AppLanguage.French -> "Nouvelle partie"
            AppLanguage.German -> "Neues Spiel"
            AppLanguage.Russian -> "Новая игра"
            AppLanguage.Arabic -> "لعبة جديدة"
        }

        Res.string.moves_remaining -> when (language) {
            AppLanguage.English -> "Moves left: ${intArg(0)}"
            AppLanguage.Turkish -> "Kalan hamle: ${intArg(0)}"
            AppLanguage.Spanish -> "Movimientos restantes: ${intArg(0)}"
            AppLanguage.French -> "Coups restants : ${intArg(0)}"
            AppLanguage.German -> "Verbleibende Züge: ${intArg(0)}"
            AppLanguage.Russian -> "Осталось ходов: ${intArg(0)}"
            AppLanguage.Arabic -> "الحركات المتبقية: ${intArg(0)}"
        }

        Res.string.select_piece -> when (language) {
            AppLanguage.English -> "Select a piece"
            AppLanguage.Turkish -> "Parça seç"
            AppLanguage.Spanish -> "Selecciona una pieza"
            AppLanguage.French -> "Sélectionnez une pièce"
            AppLanguage.German -> "Wähle ein Teil aus"
            AppLanguage.Russian -> "Выберите фигуру"
            AppLanguage.Arabic -> "اختر قطعة"
        }

        Res.string.invalid_placement -> when (language) {
            AppLanguage.English -> "Invalid placement"
            AppLanguage.Turkish -> "Geçersiz yerleştirme"
            AppLanguage.Spanish -> "Colocación no válida"
            AppLanguage.French -> "Placement invalide"
            AppLanguage.German -> "Ungültige Platzierung"
            AppLanguage.Russian -> "Недопустимое размещение"
            AppLanguage.Arabic -> "وضع غير صالح"
        }

        Res.string.invalid_placement_out_of_bounds -> when (language) {
            AppLanguage.English -> "Doesn’t fit inside the grid."
            AppLanguage.Turkish -> "Izgaranın dışına taşıyor."
            AppLanguage.Spanish -> "No cabe dentro de la cuadrícula."
            AppLanguage.French -> "La pièce ne rentre pas dans la grille."
            AppLanguage.German -> "Passt nicht in das Raster."
            AppLanguage.Russian -> "Фигура не помещается в сетку."
            AppLanguage.Arabic -> "القطعة لا تناسب داخل الشبكة."
        }

        Res.string.invalid_placement_overlap -> when (language) {
            AppLanguage.English -> "Overlaps with existing blocks."
            AppLanguage.Turkish -> "Mevcut bloklarla çakışıyor."
            AppLanguage.Spanish -> "Se superpone con bloques existentes."
            AppLanguage.French -> "Chevauche des blocs existants."
            AppLanguage.German -> "Überlappt mit vorhandenen Blöcken."
            AppLanguage.Russian -> "Перекрывает существующие блоки."
            AppLanguage.Arabic -> "تتداخل مع كتل موجودة."
        }

        Res.string.invalid_placement_adjacent_row -> when (language) {
            AppLanguage.English -> "A row can’t have more than ${intArg(0)} adjacent blocks of the same color."
            AppLanguage.Turkish -> "Bir satırda aynı renkten yan yana en fazla ${intArg(0)} blok olabilir."
            AppLanguage.Spanish -> "Una fila no puede tener más de ${intArg(0)} bloques adyacentes del mismo color."
            AppLanguage.French -> "Une ligne ne peut pas avoir plus de ${intArg(0)} blocs adjacents de la même couleur."
            AppLanguage.German -> "Eine Reihe darf nicht mehr als ${intArg(0)} benachbarte Blöcke derselben Farbe haben."
            AppLanguage.Russian -> "В строке не может быть больше ${intArg(0)} соседних блоков одного цвета."
            AppLanguage.Arabic -> "لا يمكن أن يحتوي الصف على أكثر من ${intArg(0)} كتل متجاورة من اللون نفسه."
        }

        Res.string.invalid_placement_adjacent_col -> when (language) {
            AppLanguage.English -> "A column can’t have more than ${intArg(0)} adjacent blocks of the same color."
            AppLanguage.Turkish -> "Bir sütunda aynı renkten yan yana en fazla ${intArg(0)} blok olabilir."
            AppLanguage.Spanish -> "Una columna no puede tener más de ${intArg(0)} bloques adyacentes del mismo color."
            AppLanguage.French -> "Une colonne ne peut pas avoir plus de ${intArg(0)} blocs adjacents de la même couleur."
            AppLanguage.German -> "Eine Spalte darf nicht mehr als ${intArg(0)} benachbarte Blöcke derselben Farbe haben."
            AppLanguage.Russian -> "В столбце не может быть больше ${intArg(0)} соседних блоков одного цвета."
            AppLanguage.Arabic -> "لا يمكن أن يحتوي العمود على أكثر من ${intArg(0)} كتل متجاورة من اللون نفسه."
        }

        Res.string.invalid_placement_distinct_row -> when (language) {
            AppLanguage.English -> "A full row must contain at least ${intArg(0)} different colors."
            AppLanguage.Turkish -> "Dolu bir satırda en az ${intArg(0)} farklı renk olmalı."
            AppLanguage.Spanish -> "Una fila completa debe contener al menos ${intArg(0)} colores distintos."
            AppLanguage.French -> "Une ligne complète doit contenir au moins ${intArg(0)} couleurs différentes."
            AppLanguage.German -> "Eine vollständige Reihe muss mindestens ${intArg(0)} verschiedene Farben enthalten."
            AppLanguage.Russian -> "Полная строка должна содержать как минимум ${intArg(0)} разных цветов."
            AppLanguage.Arabic -> "يجب أن يحتوي الصف المكتمل على ${intArg(0)} ألوان مختلفة على الأقل."
        }

        Res.string.invalid_placement_distinct_col -> when (language) {
            AppLanguage.English -> "A full column must contain at least ${intArg(0)} different colors."
            AppLanguage.Turkish -> "Dolu bir sütunda en az ${intArg(0)} farklı renk olmalı."
            AppLanguage.Spanish -> "Una columna completa debe contener al menos ${intArg(0)} colores distintos."
            AppLanguage.French -> "Une colonne complète doit contenir au moins ${intArg(0)} couleurs différentes."
            AppLanguage.German -> "Eine vollständige Spalte muss mindestens ${intArg(0)} verschiedene Farben enthalten."
            AppLanguage.Russian -> "Полный столбец должен содержать как минимум ${intArg(0)} разных цветов."
            AppLanguage.Arabic -> "يجب أن يحتوي العمود المكتمل على ${intArg(0)} ألوان مختلفة على الأقل."
        }

        Res.string.rule_color_limit_row -> when (language) {
            AppLanguage.English -> "A row can’t contain more than ${intArg(0)} blocks of the same color."
            AppLanguage.Turkish -> "Bir satırda aynı renkten ${intArg(0)} adetten fazla blok olamaz."
            AppLanguage.Spanish -> "Una fila no puede contener más de ${intArg(0)} bloques del mismo color."
            AppLanguage.French -> "Une ligne ne peut pas contenir plus de ${intArg(0)} blocs de la même couleur."
            AppLanguage.German -> "Eine Reihe darf nicht mehr als ${intArg(0)} Blöcke derselben Farbe enthalten."
            AppLanguage.Russian -> "Строка не может содержать больше ${intArg(0)} блоков одного цвета."
            AppLanguage.Arabic -> "لا يمكن أن يحتوي الصف على أكثر من ${intArg(0)} كتل من اللون نفسه."
        }

        Res.string.rule_color_limit_col -> when (language) {
            AppLanguage.English -> "A column can’t contain more than ${intArg(0)} blocks of the same color."
            AppLanguage.Turkish -> "Bir sütunda aynı renkten ${intArg(0)} adetten fazla blok olamaz."
            AppLanguage.Spanish -> "Una columna no puede contener más de ${intArg(0)} bloques del mismo color."
            AppLanguage.French -> "Une colonne ne peut pas contenir plus de ${intArg(0)} blocs de la même couleur."
            AppLanguage.German -> "Eine Spalte darf nicht mehr als ${intArg(0)} Blöcke derselben Farbe enthalten."
            AppLanguage.Russian -> "Столбец не может содержать больше ${intArg(0)} блоков одного цвета."
            AppLanguage.Arabic -> "لا يمكن أن يحتوي العمود على أكثر من ${intArg(0)} كتل من اللون نفسه."
        }

        Res.string.game_over -> when (language) {
            AppLanguage.English -> "Game Over"
            AppLanguage.Turkish -> "Oyun Bitti"
            AppLanguage.Spanish -> "Fin del juego"
            AppLanguage.French -> "Partie terminée"
            AppLanguage.German -> "Spiel beendet"
            AppLanguage.Russian -> "Игра окончена"
            AppLanguage.Arabic -> "انتهت اللعبة"
        }

        Res.string.game_over_message -> when (language) {
            AppLanguage.English -> "There are no valid moves left for this game."
            AppLanguage.Turkish -> "Bu oyun için geçerli bir hamle kalmadı."
            AppLanguage.Spanish -> "No quedan movimientos válidos para esta partida."
            AppLanguage.French -> "Il n’y a plus de coups valides pour cette partie."
            AppLanguage.German -> "Für dieses Spiel sind keine gültigen Züge mehr übrig."
            AppLanguage.Russian -> "Для этой игры больше не осталось допустимых ходов."
            AppLanguage.Arabic -> "لا توجد حركات صالحة متبقية لهذه اللعبة."
        }

        Res.string.ok -> when (language) {
            AppLanguage.English -> "OK"
            AppLanguage.Turkish -> "Tamam"
            AppLanguage.Spanish -> "Aceptar"
            AppLanguage.French -> "OK"
            AppLanguage.German -> "OK"
            AppLanguage.Russian -> "ОК"
            AppLanguage.Arabic -> "حسنًا"
        }

        Res.string.rules_title -> when (language) {
            AppLanguage.English -> "Block Logic Rules"
            AppLanguage.Turkish -> "Blok Mantık Kuralları"
            AppLanguage.Spanish -> "Reglas de Block Logic"
            AppLanguage.French -> "Règles de Block Logic"
            AppLanguage.German -> "Block-Logic-Regeln"
            AppLanguage.Russian -> "Правила Block Logic"
            AppLanguage.Arabic -> "قواعد Block Logic"
        }

        Res.string.rules_intro -> when (language) {
            AppLanguage.English -> "Place pieces on the grid. Complete rows or columns to clear them."
            AppLanguage.Turkish -> "Parçaları ızgaraya yerleştir. Satır veya sütunları doldurarak temizle."
            AppLanguage.Spanish -> "Coloca piezas en la cuadrícula. Completa filas o columnas para limpiarlas."
            AppLanguage.French -> "Placez des pièces sur la grille. Complétez des lignes ou des colonnes pour les effacer."
            AppLanguage.German -> "Platziere Teile auf dem Raster. Vervollständige Reihen oder Spalten, um sie zu löschen."
            AppLanguage.Russian -> "Размещайте фигуры на сетке. Заполняйте строки или столбцы, чтобы очищать их."
            AppLanguage.Arabic -> "ضع القطع على الشبكة. أكمل الصفوف أو الأعمدة لمسحها."
        }

        Res.string.rules_rule_1_title -> when (language) {
            AppLanguage.English -> "Row color limit"
            AppLanguage.Turkish -> "Satır renk limiti"
            AppLanguage.Spanish -> "Límite de color por fila"
            AppLanguage.French -> "Limite de couleur par ligne"
            AppLanguage.German -> "Farblimit pro Reihe"
            AppLanguage.Russian -> "Ограничение цвета в строке"
            AppLanguage.Arabic -> "حد اللون في الصف"
        }

        Res.string.rules_rule_1_desc_disabled -> when (language) {
            AppLanguage.English -> "Easy mode has no row color limit."
            AppLanguage.Turkish -> "Kolay modda satır renk limiti yok."
            AppLanguage.Spanish -> "El modo fácil no tiene límite de color por fila."
            AppLanguage.French -> "Le mode facile n’a pas de limite de couleur par ligne."
            AppLanguage.German -> "Im leichten Modus gibt es kein Farblimit pro Reihe."
            AppLanguage.Russian -> "В лёгком режиме нет ограничения цвета в строке."
            AppLanguage.Arabic -> "الوضع السهل لا يحتوي على حد للون في الصف."
        }

        Res.string.rules_rule_1_desc_enabled -> when (language) {
            AppLanguage.English -> "Each row can contain at most ${intArg(0)} blocks of the same color."
            AppLanguage.Turkish -> "Her satırda aynı renkten en fazla ${intArg(0)} blok olabilir."
            AppLanguage.Spanish -> "Cada fila puede contener como máximo ${intArg(0)} bloques del mismo color."
            AppLanguage.French -> "Chaque ligne peut contenir au maximum ${intArg(0)} blocs de la même couleur."
            AppLanguage.German -> "Jede Reihe darf höchstens ${intArg(0)} Blöcke derselben Farbe enthalten."
            AppLanguage.Russian -> "Каждая строка может содержать не более ${intArg(0)} блоков одного цвета."
            AppLanguage.Arabic -> "يمكن أن يحتوي كل صف على ${intArg(0)} كتل كحد أقصى من اللون نفسه."
        }

        Res.string.rules_rule_2_title -> when (language) {
            AppLanguage.English -> "Column color limit"
            AppLanguage.Turkish -> "Sütun renk limiti"
            AppLanguage.Spanish -> "Límite de color por columna"
            AppLanguage.French -> "Limite de couleur par colonne"
            AppLanguage.German -> "Farblimit pro Spalte"
            AppLanguage.Russian -> "Ограничение цвета в столбце"
            AppLanguage.Arabic -> "حد اللون في العمود"
        }

        Res.string.rules_rule_2_desc_disabled -> when (language) {
            AppLanguage.English -> "Easy mode has no column color limit."
            AppLanguage.Turkish -> "Kolay modda sütun renk limiti yok."
            AppLanguage.Spanish -> "El modo fácil no tiene límite de color por columna."
            AppLanguage.French -> "Le mode facile n’a pas de limite de couleur par colonne."
            AppLanguage.German -> "Im leichten Modus gibt es kein Farblimit pro Spalte."
            AppLanguage.Russian -> "В лёгком режиме нет ограничения цвета в столбце."
            AppLanguage.Arabic -> "الوضع السهل لا يحتوي على حد للون في العمود."
        }

        Res.string.rules_rule_2_desc_enabled -> when (language) {
            AppLanguage.English -> "Each column can contain at most ${intArg(0)} blocks of the same color."
            AppLanguage.Turkish -> "Her sütunda aynı renkten en fazla ${intArg(0)} blok olabilir."
            AppLanguage.Spanish -> "Cada columna puede contener como máximo ${intArg(0)} bloques del mismo color."
            AppLanguage.French -> "Chaque colonne peut contenir au maximum ${intArg(0)} blocs de la même couleur."
            AppLanguage.German -> "Jede Spalte darf höchstens ${intArg(0)} Blöcke derselben Farbe enthalten."
            AppLanguage.Russian -> "Каждый столбец может содержать не более ${intArg(0)} блоков одного цвета."
            AppLanguage.Arabic -> "يمكن أن يحتوي كل عمود على ${intArg(0)} كتل كحد أقصى من اللون نفسه."
        }

        Res.string.rules_piece_pool_title -> when (language) {
            AppLanguage.English -> "Available piece size"
            AppLanguage.Turkish -> "Parça boyutu havuzu"
            AppLanguage.Spanish -> "Tamaño de pieza disponible"
            AppLanguage.French -> "Taille de pièce disponible"
            AppLanguage.German -> "Verfügbare Teilgröße"
            AppLanguage.Russian -> "Доступный размер фигур"
            AppLanguage.Arabic -> "حجم القطع المتاح"
        }

        Res.string.rules_piece_pool_desc -> when (language) {
            AppLanguage.English -> "This mode can generate pieces up to ${gridSizeOptionValue(intArg(0))}."
            AppLanguage.Turkish -> "Bu modda en fazla ${gridSizeOptionValue(intArg(0))} boyutunda parçalar gelir."
            AppLanguage.Spanish -> "Este modo puede generar piezas de hasta ${gridSizeOptionValue(intArg(0))}."
            AppLanguage.French -> "Ce mode peut générer des pièces jusqu’à ${gridSizeOptionValue(intArg(0))}."
            AppLanguage.German -> "Dieser Modus kann Teile bis zu ${gridSizeOptionValue(intArg(0))} erzeugen."
            AppLanguage.Russian -> "В этом режиме могут появляться фигуры размером до ${gridSizeOptionValue(intArg(0))}."
            AppLanguage.Arabic -> "يمكن لهذا الوضع إنشاء قطع حتى حجم ${gridSizeOptionValue(intArg(0))}."
        }

        Res.string.rules_adjacent_limit_title -> when (language) {
            AppLanguage.English -> "Adjacent color limit"
            AppLanguage.Turkish -> "Bitişik renk limiti"
            AppLanguage.Spanish -> "Límite de color adyacente"
            AppLanguage.French -> "Limite de couleur adjacente"
            AppLanguage.German -> "Limit benachbarter Farben"
            AppLanguage.Russian -> "Ограничение соседних цветов"
            AppLanguage.Arabic -> "حد الألوان المتجاورة"
        }

        Res.string.rules_adjacent_limit_desc_disabled -> when (language) {
            AppLanguage.English -> "This mode has no adjacent color limit."
            AppLanguage.Turkish -> "Bu modda bitişik renk limiti yok."
            AppLanguage.Spanish -> "Este modo no tiene límite de color adyacente."
            AppLanguage.French -> "Ce mode n’a pas de limite de couleur adjacente."
            AppLanguage.German -> "Dieser Modus hat kein Limit für benachbarte Farben."
            AppLanguage.Russian -> "В этом режиме нет ограничения на соседние цвета."
            AppLanguage.Arabic -> "لا يوجد حد للألوان المتجاورة في هذا الوضع."
        }

        Res.string.rules_adjacent_limit_desc_enabled -> when (language) {
            AppLanguage.English -> "Rows and columns can have at most ${intArg(0)} adjacent blocks of the same color."
            AppLanguage.Turkish -> "Satır ve sütunlarda aynı renkten yan yana en fazla ${intArg(0)} blok olabilir."
            AppLanguage.Spanish -> "Las filas y columnas pueden tener como máximo ${intArg(0)} bloques adyacentes del mismo color."
            AppLanguage.French -> "Les lignes et les colonnes peuvent avoir au maximum ${intArg(0)} blocs adjacents de la même couleur."
            AppLanguage.German -> "Reihen und Spalten dürfen höchstens ${intArg(0)} benachbarte Blöcke derselben Farbe haben."
            AppLanguage.Russian -> "В строках и столбцах может быть не более ${intArg(0)} соседних блоков одного цвета."
            AppLanguage.Arabic -> "يمكن أن تحتوي الصفوف والأعمدة على ${intArg(0)} كتل متجاورة كحد أقصى من اللون نفسه."
        }

        Res.string.rules_variety_title -> when (language) {
            AppLanguage.English -> "Color variety"
            AppLanguage.Turkish -> "Renk çeşitliliği"
            AppLanguage.Spanish -> "Variedad de colores"
            AppLanguage.French -> "Variété de couleurs"
            AppLanguage.German -> "Farbvielfalt"
            AppLanguage.Russian -> "Разнообразие цветов"
            AppLanguage.Arabic -> "تنوع الألوان"
        }

        Res.string.rules_variety_desc_disabled -> when (language) {
            AppLanguage.English -> "This mode has no minimum color variety rule."
            AppLanguage.Turkish -> "Bu modda minimum renk çeşitliliği kuralı yok."
            AppLanguage.Spanish -> "Este modo no tiene regla mínima de variedad de color."
            AppLanguage.French -> "Ce mode n’a pas de règle minimale de variété de couleurs."
            AppLanguage.German -> "Dieser Modus hat keine Mindestregel für Farbvielfalt."
            AppLanguage.Russian -> "В этом режиме нет правила минимального разнообразия цветов."
            AppLanguage.Arabic -> "لا توجد قاعدة لحد أدنى من تنوع الألوان في هذا الوضع."
        }

        Res.string.rules_variety_desc_enabled -> when (language) {
            AppLanguage.English -> "A full row or column must contain at least ${intArg(0)} different colors."
            AppLanguage.Turkish -> "Dolu satır ve sütunlarda en az ${intArg(0)} farklı renk bulunmalı."
            AppLanguage.Spanish -> "Una fila o columna completa debe contener al menos ${intArg(0)} colores distintos."
            AppLanguage.French -> "Une ligne ou colonne complète doit contenir au moins ${intArg(0)} couleurs différentes."
            AppLanguage.German -> "Eine vollständige Reihe oder Spalte muss mindestens ${intArg(0)} verschiedene Farben enthalten."
            AppLanguage.Russian -> "Полная строка или столбец должна содержать как минимум ${intArg(0)} разных цветов."
            AppLanguage.Arabic -> "يجب أن يحتوي الصف أو العمود المكتمل على ${intArg(0)} ألوان مختلفة على الأقل."
        }

        Res.string.rules_move_limit_title -> when (language) {
            AppLanguage.English -> "Move pressure"
            AppLanguage.Turkish -> "Hamle baskısı"
            AppLanguage.Spanish -> "Presión de movimientos"
            AppLanguage.French -> "Pression des coups"
            AppLanguage.German -> "Zugdruck"
            AppLanguage.Russian -> "Ограничение ходов"
            AppLanguage.Arabic -> "ضغط الحركات"
        }

        Res.string.rules_move_limit_desc_disabled -> when (language) {
            AppLanguage.English -> "This mode has no move limit."
            AppLanguage.Turkish -> "Bu modda hamle limiti yok."
            AppLanguage.Spanish -> "Este modo no tiene límite de movimientos."
            AppLanguage.French -> "Ce mode n’a pas de limite de coups."
            AppLanguage.German -> "Dieser Modus hat kein Zuglimit."
            AppLanguage.Russian -> "В этом режиме нет лимита ходов."
            AppLanguage.Arabic -> "لا يوجد حد للحركات في هذا الوضع."
        }

        Res.string.rules_move_limit_desc_enabled -> when (language) {
            AppLanguage.English -> "You have ${intArg(0)} moves to finish the board."
            AppLanguage.Turkish -> "Oyunu bitirmek için ${intArg(0)} hamlen var."
            AppLanguage.Spanish -> "Tienes ${intArg(0)} movimientos para terminar el tablero."
            AppLanguage.French -> "Vous avez ${intArg(0)} coups pour terminer le plateau."
            AppLanguage.German -> "Du hast ${intArg(0)} Züge, um das Brett zu beenden."
            AppLanguage.Russian -> "У вас есть ${intArg(0)} ходов, чтобы завершить поле."
            AppLanguage.Arabic -> "لديك ${intArg(0)} حركات لإنهاء اللوحة."
        }

        Res.string.rules_prefilled_title -> when (language) {
            AppLanguage.English -> "Board state"
            AppLanguage.Turkish -> "Tahta durumu"
            AppLanguage.Spanish -> "Estado del tablero"
            AppLanguage.French -> "État du plateau"
            AppLanguage.German -> "Brettzustand"
            AppLanguage.Russian -> "Состояние поля"
            AppLanguage.Arabic -> "حالة اللوحة"
        }

        Res.string.rules_prefilled_desc -> when (language) {
            AppLanguage.English -> "The board starts with ${intArg(0)}% prefilled cells and ${intArg(1)}% locked cells."
            AppLanguage.Turkish -> "Tahta başlangıçta ${intArg(0)}% dolu hücre ve ${intArg(1)}% kilitli hücre içerir."
            AppLanguage.Spanish -> "El tablero empieza con ${intArg(0)}% de celdas rellenadas y ${intArg(1)}% de celdas bloqueadas."
            AppLanguage.French -> "Le plateau commence avec ${intArg(0)}% de cases préremplies et ${intArg(1)}% de cases verrouillées."
            AppLanguage.German -> "Das Brett startet mit ${intArg(0)}% vorgefüllten Zellen und ${intArg(1)}% gesperrten Zellen."
            AppLanguage.Russian -> "Поле начинается с ${intArg(0)}% заполненных клеток и ${intArg(1)}% заблокированных клеток."
            AppLanguage.Arabic -> "تبدأ اللوحة بنسبة ${intArg(0)}% من الخلايا المعبأة مسبقًا و${intArg(1)}% من الخلايا المقفلة."
        }

        Res.string.rules_tips_title -> when (language) {
            AppLanguage.English -> "Tips"
            AppLanguage.Turkish -> "İpuçları"
            AppLanguage.Spanish -> "Consejos"
            AppLanguage.French -> "Conseils"
            AppLanguage.German -> "Tipps"
            AppLanguage.Russian -> "Подсказки"
            AppLanguage.Arabic -> "نصائح"
        }

        Res.string.rules_tips_desc -> when (language) {
            AppLanguage.English -> "If none of the remaining pieces can be placed anywhere, the game ends."
            AppLanguage.Turkish -> "Kalan parçalardan hiçbiri hiçbir yere yerleştirilemiyorsa oyun biter."
            AppLanguage.Spanish -> "Si ninguna de las piezas restantes puede colocarse en ningún lugar, la partida termina."
            AppLanguage.French -> "Si aucune des pièces restantes ne peut être placée nulle part, la partie se termine."
            AppLanguage.German -> "Wenn keines der verbleibenden Teile irgendwo platziert werden kann, endet das Spiel."
            AppLanguage.Russian -> "Если ни одну из оставшихся фигур нельзя разместить, игра заканчивается."
            AppLanguage.Arabic -> "إذا لم يعد بالإمكان وضع أي من القطع المتبقية في أي مكان، تنتهي اللعبة."
        }

        Res.string.rules_current_mode -> when (language) {
            AppLanguage.English -> "Current mode: ${textArg(0)} • ${textArg(1)}"
            AppLanguage.Turkish -> "Geçerli mod: ${textArg(0)} • ${textArg(1)}"
            AppLanguage.Spanish -> "Modo actual: ${textArg(0)} • ${textArg(1)}"
            AppLanguage.French -> "Mode actuel : ${textArg(0)} • ${textArg(1)}"
            AppLanguage.German -> "Aktueller Modus: ${textArg(0)} • ${textArg(1)}"
            AppLanguage.Russian -> "Текущий режим: ${textArg(0)} • ${textArg(1)}"
            AppLanguage.Arabic -> "الوضع الحالي: ${textArg(0)} • ${textArg(1)}"
        }

        Res.string.scores_title -> when (language) {
            AppLanguage.English -> "Scores"
            AppLanguage.Turkish -> "Puanlar"
            AppLanguage.Spanish -> "Puntuaciones"
            AppLanguage.French -> "Scores"
            AppLanguage.German -> "Punkte"
            AppLanguage.Russian -> "Рекорды"
            AppLanguage.Arabic -> "النتائج"
        }

        Res.string.scores_best_for_mode -> when (language) {
            AppLanguage.English -> "Best score"
            AppLanguage.Turkish -> "En iyi skor"
            AppLanguage.Spanish -> "Mejor puntuación"
            AppLanguage.French -> "Meilleur score"
            AppLanguage.German -> "Bester Punktestand"
            AppLanguage.Russian -> "Лучший счёт"
            AppLanguage.Arabic -> "أفضل نتيجة"
        }

        Res.string.scores_empty -> "-"

        Res.string.selected_mode_best_score -> when (language) {
            AppLanguage.English -> "Best for this mode: ${textArg(0)}"
            AppLanguage.Turkish -> "Bu mod için en iyi skor: ${textArg(0)}"
            AppLanguage.Spanish -> "Mejor resultado para este modo: ${textArg(0)}"
            AppLanguage.French -> "Meilleur score pour ce mode : ${textArg(0)}"
            AppLanguage.German -> "Bester Wert für diesen Modus: ${textArg(0)}"
            AppLanguage.Russian -> "Лучший результат для этого режима: ${textArg(0)}"
            AppLanguage.Arabic -> "أفضل نتيجة لهذا الوضع: ${textArg(0)}"
        }

        Res.string.block_style_bubble -> when (language) {
            AppLanguage.English -> "Bubble"
            AppLanguage.Turkish -> "Baloncuk"
            AppLanguage.Spanish -> "Burbuja"
            AppLanguage.French -> "Bulle"
            AppLanguage.German -> "Blase"
            AppLanguage.Russian -> "Пузырь"
            AppLanguage.Arabic -> "فقاعة"
        }

        Res.string.block_style_outline -> when (language) {
            AppLanguage.English -> "Outline"
            AppLanguage.Turkish -> "Çizgisel"
            AppLanguage.Spanish -> "Contorno"
            AppLanguage.French -> "Contour"
            AppLanguage.German -> "Kontur"
            AppLanguage.Russian -> "Контур"
            AppLanguage.Arabic -> "مخطط"
        }

        Res.string.block_style_wood -> when (language) {
            AppLanguage.English -> "Wood"
            AppLanguage.Turkish -> "Tahta"
            AppLanguage.Spanish -> "Madera"
            AppLanguage.French -> "Bois"
            AppLanguage.German -> "Holz"
            AppLanguage.Russian -> "Дерево"
            AppLanguage.Arabic -> "خشب"
        }

        else -> error("Unsupported localized string resource: $resource")
    }
}

