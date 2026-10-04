// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

import org.gradle.api.Project
import java.util.Properties

object InfluentialEdition {
    const val PROPERTY = "influential.edition"
    const val PERSONAL = "personal"
    const val PLAY = "play"
    const val FLAG = "PERSONAL_EDITION"
    const val SECRETS_FILE = "secrets.properties"
}

val Project.isPersonalEdition: Boolean
    get() = (findProperty(InfluentialEdition.PROPERTY)?.toString() ?: InfluentialEdition.PERSONAL) != InfluentialEdition.PLAY

fun Project.personalSecret(name: String): String {
    if (!isPersonalEdition) return ""
    val file = rootProject.file(InfluentialEdition.SECRETS_FILE)
    if (!file.isFile) return ""
    val secrets = Properties().apply { file.inputStream().use { load(it) } }
    return secrets.getProperty(name, "").trim()
}
