package tekin.luetfi.amorfati.utils

import tekin.luetfi.amorfati.domain.model.sealed
import tekin.luetfi.amorfati.domain.model.sealedWith

object Deck {

    val cards = listOf(
        "Ale Of The Ache".sealed(),
        "Council Of Three".sealed(),
        "Designated Driver".sealed(),
        "Duchess And The White Hound".sealed(),
        "Focus Of Fate".sealed(),
        "Gaze Of Exhaustion".sealed(),
        "Gentle Highlander".sealed(),
        "Gentleman Of The Alps".sealed(),
        "Keeper Of The Bitter Truth".sealed(),
        "Keepers Of The Sky Needle".sealed(),
        "Lavender Trio".sealed(),
        "Nine Of Hounds".sealed(),
        "Nomad Of Tracks".sealed(),
        "Sisters of the Seven Tea".sealed(),
        "Round Trip Knight".sealed(),
        "Sentinel Of The Twin Towers".sealed(),
        "Silent Judge".sealed(),
        "Sister Sinister".sealed(),
        "Snow Mexican".sealed(),
        "Teacher Of Treachery".sealed(),
        "The Alchemist".sealed(),
        "The Baddies".sealed(),
        "The Baddies II".sealed(),
        "The Baddies III".sealed(),
        "The Ballad".sealed(),
        "The Dealer".sealed(),
        "The Distant Liaison".sealed(),
        "The Endless Pour".sealed(),
        "The Librarian".sealed(),
        "The Lone Star".sealed(),
        "The Rule Of Threes".sealed(),
        "The Texan & The Thistle" sealedWith "THE_TEXAN_AND_THE_THISTLE"
    )

    val f8Cards = listOf(
        "Queen Of Whatever".sealed(),
        "Countess Of Chaos".sealed(),
        "The Duchess Of Drama".sealed(),
        "Maiden Of Mischief".sealed(),
        "Lady Of Lunacy".sealed(),
        "Lady Of The First Flame".sealed(),
        "Mistress Of Mayhem".sealed(),
        "Wench Of Whiskey".sealed()
    )

    val locationCards = listOf(
        "Tehran".sealed(),
        "Hong Kong".sealed(),
        "Baghdad".sealed(),
        "Hanoi".sealed(),
        "Seattle".sealed(),
        "New York".sealed(),
        "Athens".sealed(),
        "Bangkok".sealed(),
        "Helsinki".sealed(),
        "Bielefeld".sealed(),
        "Berlin".sealed(),
        "Prague".sealed(),
        "Stockholm".sealed(),
        "Moscow".sealed(),
        "Rome".sealed(),
        "Brazil".sealed(),
        "Barcelona".sealed(),
        "Nairobi".sealed(),
        "Edinburgh".sealed(),
        "Zermatt".sealed(),
        "Johannesburg".sealed(),
        "Las Vegas".sealed(),
        "Erfurt".sealed(),
        "Leipzig".sealed(),
        "Antalya".sealed(),
        "Amsterdam".sealed(),
        "Boston".sealed(),
        "Florence".sealed(),
        "Busan".sealed(),
        "Tokyo".sealed(),
        "Tbilisi".sealed(),
        "Bruges".sealed(),
        "Vienna".sealed(),
        "San Francisco".sealed(),
        "Dublin".sealed(),
        "Copenhagen".sealed(),
        "Madrid".sealed(),
        "Dubai".sealed(),
        "Versailles".sealed(),
        "Istanbul".sealed(),
        "Sheffield".sealed(),
        "Dresden".sealed(),
        "Jerusalem".sealed(),
        "Mumbai".sealed(),
        "Venice".sealed(),
        "London".sealed(),
        "Hamburg".sealed()
    )

    var fullDeck = cards + f8Cards + locationCards

    var scannableDeck = cards + f8Cards
}