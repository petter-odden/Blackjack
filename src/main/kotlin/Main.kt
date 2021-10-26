import java.io.File
import kotlin.system.exitProcess

var cardDeck: MutableList<String> = mutableListOf()
var samsCards: MutableList<String> = mutableListOf()
var dealersCards: MutableList<String> = mutableListOf()

fun main(args: Array<String>) {

    // Get the deck for the game
    if (args.isEmpty()) {
        cardDeck = fetchDeckFromFile()
        shuffleCards(cardDeck)
    } else {
        cardDeck = fetchDeckFromFile(args[0])
    }

    // Start of game
    println("Dealing cards...\n")
    dealCards(cardDeck, samsCards)
    dealCards(cardDeck, dealersCards)
    dealCards(cardDeck, samsCards)
    dealCards(cardDeck, dealersCards)
    update()

    // Sams turn
    while (calculateScore(samsCards) < 17) {
        println("Will Sam hit? y/n")
        val answer = readLine()?.lowercase()?.trim()
        if (answer == "y") {
            // Hit
            dealCards(cardDeck, samsCards)
            update()
        } else if (answer == "n") {
            // Stand
            break
        } else {
            println("Please answer y or n")
        }

    }
    println("Sam stands, Dealers turn...\n")

    // Dealers turn
    while (true) {
        if (calculateScore(dealersCards) > calculateScore(samsCards)) {
            println("Dealer stands\n")
            endGame("dealer")
        }
        while (calculateScore(dealersCards) <= calculateScore(samsCards)) {
            println("Dealer hits...")
            dealCards(cardDeck, dealersCards)
            update()
        }
    }
}

fun fetchDeckFromFile(fileName: String = "fullDeck.csv"): MutableList<String> {
    var fileCards = mutableListOf<String>()
    // Implement check to make sure provided file is of right format?
    File(fileName).forEachLine { fileCards = it.split(",") as MutableList<String> }

    fileCards.forEachIndexed { index, element ->
        fileCards[index] = element.trim()
    }
    return fileCards
}

fun shuffleCards(deck: MutableList<String>) {
    println("Shuffling cards...")
    deck.shuffle()
}

fun dealCards(sourceDeck: MutableList<String>, receiver: MutableList<String>, amount: Int = 1) {
    var x: Int
    if (amount > sourceDeck.size) {
        x = sourceDeck.size
        println("Not enough cards in the deck!")
        println("Dealing $x cards.")
    } else {
        x = amount
    }
    while (x >= 1) {
        receiver.add(sourceDeck.removeFirst())
        x--
    }
}

fun calculateScore(hand: MutableList<String>): Int {
    // Calculates the score of a given hand of cards
    val values: MutableList<String> = mutableListOf()
    var sum = 0
    hand.forEach {
        values.add(it.substring(1))
    }
    values.forEach {
        try {
            sum += it.toInt()
        } catch (e: NumberFormatException) {
            when (it) {
                "J", "Q", "K" -> sum += 10
                // An ace's value can be 11 or 1, depending on the hand
                // The task specifically states that A = 11, so I will omit this logic
                "A" -> sum += 11
            }
        }
    }

    return sum
}

fun update() {
    // Prints the current hands and values, and checks for game-enders

    println("Sam: ${samsCards.joinToString(", ")}")
    println("Dealer: ${dealersCards.joinToString(", ")}")
    println("\nSam: ${calculateScore(samsCards)}, Dealer: ${calculateScore(dealersCards)}\n")

    // Both players get blackjack
    if (
        (calculateScore(samsCards) == 21 && calculateScore(dealersCards) == 21) && (samsCards.size == 2 && dealersCards.size == 2)) {
        println("Double blackjack!\n")
        endGame("sam")
    }
    // Both players get 21
    if (calculateScore(samsCards) == 21 && calculateScore(dealersCards) == 21) {
        println("Stalemate\n")
        endGame("no winner")
    }
    // Both bust on first hand (A+A)
    if (calculateScore(samsCards) == 22 && calculateScore(dealersCards) == 22) {
        println("Double bust!\n")
        endGame("dealer")
    }
    // Sam gets blackjack
    if (calculateScore(samsCards) == 21 && samsCards.size == 2) {
        println("Blackjack!\n")
        endGame("sam")
    }
    // Sam busts
    if (calculateScore(samsCards) > 21) {
        println("Sam busts!\n")
        endGame("dealer")
    }
    // Dealer gets blackjack
    if (calculateScore(dealersCards) == 21 && dealersCards.size == 2) {
        println("Blackjack!\n")
        endGame("dealer")
    }
    // Dealer busts
    if (calculateScore(dealersCards) > 21) {
        println("Dealer busts!\n")
        endGame("sam")
    }
}

fun endGame(winner: String) {
    println(winner)
    println("sam: ${samsCards.joinToString(", ")}")
    println("dealer: ${dealersCards.joinToString(", ")}")
    exitProcess(0)
}