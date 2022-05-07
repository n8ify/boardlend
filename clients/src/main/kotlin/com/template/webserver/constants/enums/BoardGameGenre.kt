package com.template.webserver.constants.enums

enum class BoardGameGenre(val code: String) {

    /**
     * Board games with no theme at all, or what theme is offered is so disconnected from the actual experience of playing
     * that it might as well not be there. Draughts and Go are the purest examples of abstracts, while chess -
     * with its set of named pieces and suggestion of historical warfare - is relatively thematic by the standards of the category.
     * Examples: Draughts, chess, Go, Tak, Shobu, Hive, Santorini, Azul and its sequels.
     * */
    Abstract("G1"),

    /**
     * Board games with some form of map or board defining a space that players compete to dominate,
     * usually through adding their own pieces to regions or areas or removing opponents’ pieces.
     * Sometimes the control can come through denying access to areas rather than taking them yourself -
     * it could be argued that Scrabble is an example of the genre!
     * Examples: Small World, Risk, Nanty Narking, Blood Rage.
     * */
    AreaControl("G2"),


    /**
     * Campaign board games are defined by individual plays following a series of connected scenarios,
     * where the actions and outcome of one scenario will usually affect the next.
     * Legacy board games are a specific type of campaign game where your choices and actions cause you to make permanent (often physical)
     * changes to the game and its components, such as applying stickers to the board or tearing up cards,
     * often providing a one-time experience.
     * Examples: Gloomhaven, Pandemic Legacy, Charterstone, Betrayal Legacy.
     * */
    Campaign("G3"),


    /**
     * Each player starts with their own identical deck of cards,
     * but alters it during play, with more powerful cards being added to the deck and less powerful ones removed.
     * Deckbuilders are sometimes conflated with deck construction games such as trading card games,
     * with the difference being that in deckbuilders the act of creating and customising your deck
     * is part of the core gameplay experience, instead of something that usually happens away from the table between plays.
     * Examples: Dominion, Star Realms, Undaunted: Normandy, Harry Potter: Hogwarts Battle.
     * */
    DeckBuilder("G4"),


    /**
     * A type of board game where the players use different decks of cards to play,
     * constructed prior to the game from a large pool of options, according to specific rules.
     * There are two main distribution models: trading or collectible card games sell booster pack products with a randomised set of cards in each,
     * while living card games and expandable deck games provide a fixed set of cards in each expansion.
     * (Living card game applies specifically to such games produced by Fantasy Flight Games, which has trademarked the term.)
     * Examples: Magic: The Gathering, Android: Netrunner, Marvel Champions, Arkham Horror: The Card Game.
     * */
    DeckConstruction("G5"),


    /**
     * Board games involving physical skill, whether using the whole body as in Twister or just the fingers for moving things
     * about, as with removing blocks in Jenga. This can include flicking discs or other objects with your fingers
     * like Flick ‘em Up, balancing things in games such as Beasts of Balance or even throwing objects around,
     * like Dungeon Fighter.
     * Examples: Cube Quest, Catacombs, Flip Ships, Flick ‘em Up, crokinole, Beasts of Balance.
     * */
    Dexterity("G6"),


    /**
     * Drafting is a mechanic where players are presented with a set of options (usually cards, though sometimes dice)
     * from which they must pick one, leaving the remainder for the next player to choose from.
     * The selection may be made from a shared central pool of choices, or from a hand of cards passed between players.
     * This can be a small part of a game, such as selecting an ability for use during a round,
     * or the entire decision space for a game.
     * Examples: 7 Wonders, Sushi Go!, Villagers.
     * */
    Drafting("G7"),


    /**
     * Players take the roles of characters making their way through a location,
     * often depicted by a map with a square grid or a page in a book, defeating enemies controlled by another player,
     * a companion app or the game system itself.
     * Examples: Gloomhaven, Mansions of Madness, Star Wars: Imperial Assault, Mice and Mystics.
     * */
    DungeonCrawler("G8"),


    /**
     * Over the course of an engine-building board game, you’ll build
     * an “engine”: something that takes your starting resources and/or actions and turns them into more resources,
     * which turn into even more resources, which - somewhere along the line - will usually turn into a form of victory points.
     * Examples: Res Arcana, Century: Spice Road, Race for the Galaxy.
     * */
    EngineBuilder("G9"),

    /**
     * Often shortened to just ‘Euro’, these are strategy-focused board games that prioritise limited-randomness over theme.
     * Usually competitive with interaction between players through passive competition rather than aggressive conflict.
     * Named for the fact many of the early games of this style were developed in Europe - particularly Germany -
     * in contrast to the more thematic but chance-driven “American-style” games of the time.
     * (Sometimes referred to as 'Ameritrash' by those who dislike the high luck element.)
     * Examples: Agricola, Paladins of the West Kingdom.
     * */
    EuroGame("G10"),

    /**
     * Board games that invite you to take ever bigger risks to achieve increasingly valuable rewards -
     * or to decide to keep what you’ve got before you lose everything.
     * Think the card game blackjack or deciding whether to give an uncertain answer on Who Wants to be a Millionaire?
     * Sometimes also called press-your-luck.
     * Examples: The Quacks of Quedlinburg, Port Royal, Deep Sea Adventure.
     * */
    PushYourLuck("G11"),

    /**
     * Board games where you roll one or more dice and move that many spaces -
     * commonly on a looping track of spaces, or a path with a start and finish.
     * Often landing on certain spaces will trigger specific actions or offer the player certain gameplay options.
     * Simple as that.
     * Examples: Monopoly, The Game of Life, Snakes and Ladders, Formula D.
     * */
    RollAndMove("G12"),

    /**
     * Roll some dice and decide how to use the outcome,
     * writing it into a personal scoring sheet. Each decision impacts your options for the rest of the game,
     * so even in games where everyone uses the same dice, slightly different choices at the start
     * can lead to very different end results. Some games twist the name by replacing the dice with something like cards
     * for a ‘flip-and-write’ (Welcome To…) or the writing with something like placing miniatures for a ‘roll-and-build’ (Era: Medieval Age).
     * Examples: Yahtzee, Railroad Ink, Ganz Schon Clever, Corinth.
     * */
    RollAndWrite("G13"),

    /**
     * One or more players around the table have a secret, and the rest of you need to figure out who!
     * Expect lying, bluffing and wild accusations all round. Players are often secretly assigned hidden roles
     * that only they know, and must achieve their own objectives - commonly either finding the odd one out,
     * or hiding the fact that you are the odd one out yourself.
     * Examples: Blood on the Clocktower, One Night Ultimate Werewolf, The Resistance.
     * */
    SocialDeduction("G14"),

    /**
     * Board games with a focus on narrative and description that is directed or fully created by the players.
     * This could be an overarching story lasting the whole game - or across a campaign of multiple sessions -
     * read from pre-written passages, or a sequence of vignettes as players are tasked with inventing and
     * describing something prompted by a single card.
     * Examples: The King’s Dilemma, Tales of the Arabian Nights.
     * */
    Storytelling("G15"),

    /**
     * Board games where you choose actions from spaces on the board by assigning your pool of “workers” -
     * often thematically actual workers in your employ - to them. Usually Eurogames, with player interaction created
     * because actions one player has taken often can’t be taken by or come with a cost for anyone else.
     * Examples: Charterstone, Agricola, Caverna, Lords of Waterdeep.
     * */
    WorkerPlacement("G16"),

    /**
     * Players pit armies against each other, represented by collections of miniatures or tokens on a map,
     * with a grid or actual measured distances for movement. Eliminate the opponent’s figures or achieve objectives to win,
     * with combat usually dictated by dice rolls or card play.
     * Examples: Warhammer 40,000, Memoir ‘44, Risk, Axis & Allies.
     * */
    WarGame("G17"),
}