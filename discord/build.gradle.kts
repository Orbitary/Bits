dependencies {
    api(project(":api"))
    api(project(":discord:module:command"))
    api(discordLibs.jda)
}

allprojects {
    group = "xyz.orbitary.bits.discord"

}
