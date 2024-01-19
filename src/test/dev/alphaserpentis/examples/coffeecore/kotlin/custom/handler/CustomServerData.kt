package dev.alphaserpentis.examples.coffeecore.kotlin.custom.handler

import dev.alphaserpentis.coffeecore.data.entity.ServerData

class CustomServerData : ServerData {
    var customData = "This is custom data!"

    constructor()
    constructor(onlyEphemeral: Boolean) : super(onlyEphemeral)
    constructor(onlyEphemeral: Boolean, customData: String) : super(onlyEphemeral) {
        this.customData = customData
    }
}
