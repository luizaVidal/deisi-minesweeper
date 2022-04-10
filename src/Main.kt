/* developed by:
Miguel Silva and Luiza Vidal */

const val invalidInput = "Invalid response.\n"
val linesRange = 4..9
val columnsRange = 4..9

fun makeTerrain(matrixTerrain: Array<Array<Pair<String, Boolean>>>, showLegend: Boolean, withColor: Boolean,
                showEverything: Boolean): String {
    val esc: String
    var startColor = ""
    var endColor = ""
    if (withColor) {
        esc = "\u001B"
        startColor = "$esc[97;44m"
        endColor = "$esc[0m"
    }
    if (showEverything){
        for (line in 0 until matrixTerrain.size) {
            for (column in 0 until matrixTerrain[0].size) {
                matrixTerrain[line][column] = Pair(matrixTerrain[line][column].first, true)
            }
        }
    }

    //var arrayTerrain = Array(matrixTerrain.size) {Array(matrixTerrain[0].size) {" "} }
    var terrain = ""
    if (showLegend) {
        terrain += "$startColor    ${createLegend(matrixTerrain[0].size)}    $endColor"
        terrain += "\n"
    }
    for (line in 0 until matrixTerrain.size) {
        if (showLegend) {
            terrain += "$startColor " + ('1' + line) + " $endColor"
        }
        for (column in 0 until matrixTerrain[line].size) {
            if (column == matrixTerrain[line].size - 1) {
                if (matrixTerrain[line][column].second) {
                    terrain += " ${matrixTerrain[line][column].first} "
                } else {
                    terrain += "   "
                }
            } else {
                if (matrixTerrain[line][column].second){
                    terrain += " ${matrixTerrain[line][column].first} |"
                } else {
                    terrain += "   |"
                }

            }
        }
        if (showLegend) {
            terrain += "$startColor   $endColor"
        }
        if (line < matrixTerrain.size - 1) {
            if (showLegend) {
                terrain += "\n$startColor   $endColor"
            } else {
                terrain += println()
            }
            terrain += drawLinesBetweenColumns(matrixTerrain, line)
            if (showLegend) {
                terrain += "$startColor   $endColor\n"
            } else {
                terrain += println()
            }

        }
    }
    if (showLegend) {
        terrain += "\n$startColor   "
        for (column in 0 until matrixTerrain[0].size) {
            terrain += "    "
        }
        terrain += "  $endColor"
    }
    return terrain
}

fun drawLinesBetweenColumns(matrixTerrain: Array<Array<Pair<String, Boolean>>>, line: Int): String {
    var addingToTerrain = ""
    for (column in 0 until matrixTerrain[line].size) {
        if (column == matrixTerrain[line].size - 1) {
            addingToTerrain += "---"   // last column
        } else {
            addingToTerrain += "---+"
        }
    }
    return addingToTerrain
}

fun createMatrixTerrain(numLines: Int, numColumns: Int, numMines: Int,
                        ensurePathToWin: Boolean): Array<Array<Pair<String, Boolean>>> {
    val matrixTerrain = Array(numLines) {Array(numColumns) {Pair(" ", false)} }
    var placedMines = 0
    matrixTerrain[0][0] = Pair("P", true)
    matrixTerrain[matrixTerrain.size - 1][matrixTerrain[0].size - 1] = Pair("f", true)
    while (placedMines < numMines) {
        if (ensurePathToWin) {

            val randomCoordinate: Pair<Int, Int> = Pair((0 until numLines).random(),
                (0 until numColumns).random())
            val coordsSquareAroundPoint = getSquareAroundPoint(randomCoordinate.first,
                randomCoordinate.second, matrixTerrain.size, matrixTerrain[0].size)
            val yTopLeft = coordsSquareAroundPoint.first.first
            val xTopLeft = coordsSquareAroundPoint.first.second
            val yBotRight = coordsSquareAroundPoint.second.first
            val xBotRight = coordsSquareAroundPoint.second.second
            if (matrixTerrain[randomCoordinate.first][randomCoordinate.second].first == " ") {
                if (isEmptyAround(matrixTerrain, randomCoordinate.first, randomCoordinate.second, yTopLeft,
                        xTopLeft, yBotRight, xBotRight)){
                    matrixTerrain[randomCoordinate.first][randomCoordinate.second] = Pair("*", false)
                    placedMines++
                }
            }
        } else {
            val randomCoordinate: Pair<Int, Int> = Pair((0 until numLines).random(),
                (0 until numColumns).random())
            if (randomCoordinate != Pair(0, 0) && randomCoordinate != Pair(numLines - 1, numColumns - 1)
                && matrixTerrain[randomCoordinate.first][randomCoordinate.second].first != "*") {
                matrixTerrain[randomCoordinate.first][randomCoordinate.second] = Pair("*", false)
                placedMines++
            }
        }
    }
    return matrixTerrain
}

fun getSquareAroundPoint(linha: Int, coluna: Int, numLines: Int, numColumns: Int): Pair<Pair<Int, Int>, Pair<Int,
        Int>> {
    val xRange = 0 until numLines
    val yRange = 0 until numColumns
    var topLeftCoordinate = Pair(linha, coluna)
    var bottomRightCoordinate = Pair(linha, coluna)

    if (linha - 1 in xRange) {
        topLeftCoordinate = Pair(linha - 1, topLeftCoordinate.second)
    }
    if (coluna - 1 in yRange) {
        topLeftCoordinate = Pair(topLeftCoordinate.first, coluna - 1)
    }
    if (linha + 1 in xRange) {
        bottomRightCoordinate = Pair(linha + 1, bottomRightCoordinate.second)
    }
    if (coluna + 1 in yRange) {
        bottomRightCoordinate = Pair(bottomRightCoordinate.first, coluna + 1)
    }

    return Pair(topLeftCoordinate, bottomRightCoordinate)

}

fun countNumberOfMinesCloseToCurrentCell(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerX: Int,
                                         centerY: Int): Int {
    var mineCounter = 0
    for (line in centerX - 1..centerX + 1) {
        for (column in centerY - 1..centerY + 1) {
            if (isCoordinateInsideTerrain(Pair(line, column), matrixTerrain.size, matrixTerrain[0].size)) {
                if (matrixTerrain[line][column].first[0] == '*' && Pair(line, column) != Pair(centerX, centerY)) {
                    mineCounter++
                }
            }
        }
    }
    return mineCounter
}

fun revealMatrix(matrixTerrain: Array<Array<Pair<String, Boolean>>>, coordY: Int, coordX: Int,
                 endGame: Boolean = false) {
    val lineRange = coordY - 1 .. coordY + 1
    val columnRange = coordX - 1 .. coordX + 1
    for (line in lineRange) {
        for (column in columnRange) {
            if (line in 0 until matrixTerrain.size && column in 0 until matrixTerrain[0].size) {
                if (endGame || matrixTerrain[line][column].first != "*") {
                    matrixTerrain[line][column] = Pair(matrixTerrain[line][column].first, true)
                }
            }
        }
    }



}

fun getCurrentPlayerCoord(matrixTerrain: Array<Array<Pair<String, Boolean>>>) : Pair<Int, Int> {
    for (line in 0 until matrixTerrain.size) {
        for (column in 0 until matrixTerrain[0].size) {
            if (matrixTerrain[line][column].first == "P") {
                return Pair(line, column)
            }
        }
    }
    return Pair(-1, -1) //should never happen
}

fun fillNumberOfMines(matrixTerrain: Array<Array<Pair<String, Boolean>>>) {
    for (line in 0 until matrixTerrain.size) {
        for (column in 0 until matrixTerrain[line].size) {
            if (matrixTerrain[line][column].first == "*") {
                //do nothing
            } else if (matrixTerrain[line][column].first == "P" || matrixTerrain[line][column].first == "f") {
                //do nothing
            } else if (countNumberOfMinesCloseToCurrentCell(matrixTerrain, line, column).toString() == "0") {
                matrixTerrain[line][column] = Pair(" ", true)
            } else {
                matrixTerrain[line][column] = Pair(countNumberOfMinesCloseToCurrentCell(matrixTerrain, line, column).toString(), false)
            }
        }
    }
}

fun isEmptyAround(matrixTerrain: Array<Array<Pair<String, Boolean>>>, centerY: Int, centerX: Int, yl: Int, xl: Int,
                  yr: Int, xr: Int): Boolean {
    for (line in yl..yr){
        for (column in xl..xr){
            if (matrixTerrain[line][column].first != " " &&
                (!(line == centerY && column == centerX))) {
                return false
            }
        }
    }
    return true
}

fun isMovementPValid(currentCoord : Pair<Int, Int>, targetCoord : Pair<Int, Int>): Boolean {
    val pointCoordinateX = currentCoord.first
    val pointCoordinateY = currentCoord.second
    if (targetCoord == Pair(pointCoordinateX - 1, pointCoordinateY)) { //up
        return true
    }
    if (targetCoord == Pair(pointCoordinateX - 1, pointCoordinateY - 1)) { //up left diagonal
        return true
    }
    if (targetCoord == Pair(pointCoordinateX , pointCoordinateY - 1)) { //left
        return true
    }
    if (targetCoord == Pair(pointCoordinateX + 1, pointCoordinateY - 1)) { //down left diagonal
        return true
    }
    if (targetCoord == Pair(pointCoordinateX + 1, pointCoordinateY)) { //down
        return true
    }
    if (targetCoord == Pair(pointCoordinateX + 1, pointCoordinateY + 1)) { //down right diagonal
        return true
    }
    if (targetCoord == Pair(pointCoordinateX, pointCoordinateY + 1)) { //right
        return true
    }
    return false
} //clean up please

fun isCoordinateInsideTerrain(coord: Pair<Int, Int>, numColumns: Int, numLines: Int): Boolean {
    if (coord.first in 0 until numLines && coord.second in 0 until numColumns) {
        return true
    }
    return false
}

fun getCoordinates (readText:String?): Pair<Int, Int>? {
    val xCoord: Int
    val yCoord: Int

    if (readText != null) {
        if (readText.length != 2 || !(readText.first().isDigit()) || !(readText.last().isLetter()) || readText[0].toString().toInt() == 0) {
            return null
        } else {
            xCoord = (readText[0] - 1).toString().toInt()
            var counter = 0
            var character = 'A'
            while (readText[1].toUpperCase() != character) {
                character++
                counter++
            }
            /*
            if (xCoord < 0 || yCoord< 0) {
                return null
            }

             */
            yCoord = counter
            return Pair(xCoord, yCoord)
        }
    }
    return null
}

fun createLegend(numColumns: Int): String {
    var legend = ""
    var legendColumnCounter = 0
    while (legendColumnCounter < numColumns) {
        if (legendColumnCounter == numColumns - 1) {
            legend += ('A' + legendColumnCounter)
        } else {
            legend += ('A' + legendColumnCounter) + "   "
        }
        legendColumnCounter++
    }
    return legend
}

fun isNameValid(name: String?, minLength: Int = 3): Boolean {
    var spacePosition = -1
    var numberOfSpaces = 0
    var upperCaseCounter = 0
    var counter = 0
    if (name != null) {
        while (counter < name.length) {

            if (name[counter] == ' ') {
                spacePosition = counter
                numberOfSpaces++
            }

            if (name[counter].isUpperCase()){
                upperCaseCounter++
            }
            counter++

        }
        if (spacePosition == -1) {
            return false
        }
        if (numberOfSpaces > 1){
            return false
        }
        if (spacePosition == name.length -1 || spacePosition < minLength || upperCaseCounter > 2 ||
            !name[0].isUpperCase() || !name[spacePosition + 1].isUpperCase()) {
            return false
        }
        return true
    }
    return false
}

fun calculateNumMinesForGameConfiguration(numLines: Int, numColumns:Int): Int? {
    when (numLines * numColumns - 2) {
        in 14..20 -> return 6
        in 21..40 -> return 9
        in 41..60 -> return 12
        in 61..79 -> return 19
    }
    return null
}

fun isValidGameMinesConfiguration(numLines: Int, numColumns: Int, numMines: Int): Boolean {
    if (numMines <= 0) {
        return false
    }
    val freeSpots = numLines * numColumns - 2
    if (freeSpots < numMines) {
        return false
    }
    return true
}

fun makeMenu(): String {
    return """
            
            Welcome to DEISI Minesweeper
        
            1 - Start New Game
            0 - Exit Game
        
        """.trimIndent()
}

fun getMenuChoice(): Int {
    var menuChoice : Int? = -1
    while (menuChoice != 1 && menuChoice != 0) {
        println(makeMenu())
        menuChoice = readLine()?.toIntOrNull() ?:-1
        if (menuChoice != 1 && menuChoice != 0) {
            println(invalidInput)
        }
    }
    return menuChoice
}

fun getPlayerName(): String {
    var playerName = "-1"
    while (!isNameValid(playerName)) {
        println("Enter player name:")
        playerName = readLine() ?:"-1"
        if (!isNameValid(playerName)) {
            println(invalidInput)
        }
    }
    return playerName
}

fun getLegendChoice(): Boolean {
    var legendChoice = "-1"
    while (legendChoice != "y" && legendChoice != "n") {
        println("Show legend? (y/n)")
        legendChoice = readLine()?.toLowerCase() ?: "-1"
        if (legendChoice != "y" && legendChoice != "n") {
            println(invalidInput)
        }
    }
    var showLegend = false
    if (legendChoice == "y") {
        showLegend = true
    }
    return showLegend
}

fun getNumLines(): Int {
    var numLines = -1
    while (numLines !in linesRange) {
        println("How many lines?")
        numLines = readLine()?.toIntOrNull() ?:-1
        if (numLines !in linesRange) {
            println(invalidInput)
        }
    }
    return numLines
}

fun getNumColumns(): Int {
    var numColumns = -1
    while (numColumns !in columnsRange) {
        println("How many columns?")
        numColumns = readLine()?.toIntOrNull() ?:-1
        if (numColumns !in columnsRange) {
            println(invalidInput)
        }
    }
    return numColumns
}

fun getNumMines(numLines: Int, numColumns: Int): Int {
    var numMines = -1
    while (!isValidGameMinesConfiguration(numLines, numColumns, numMines)) {
        println("How many mines? (press enter for default value)")
        var tempNumMines = readLine()
        if (tempNumMines == "") {
            tempNumMines = (calculateNumMinesForGameConfiguration(numLines, numColumns)).toString()
        }
        if (tempNumMines?.toIntOrNull() != null) {
            numMines = tempNumMines.toInt()
        }
        if (!isValidGameMinesConfiguration(numLines, numColumns, numMines)) {
            println(invalidInput)
        }
    }
    return numMines
}

fun main() {

    val menuChoice = getMenuChoice()

    if (menuChoice == 0) {
        return
    }

    val playerName = getPlayerName()

    val showLegend = getLegendChoice()

    val numLines = getNumLines()

    val numColumns = getNumColumns()

    val numMines = getNumMines(numLines, numColumns)

    val matrixTerrain = createMatrixTerrain(numLines, numColumns, numMines, false)
    fillNumberOfMines(matrixTerrain)
    revealMatrix(matrixTerrain, 0, 0)

    var gameEnded = false
    while (!gameEnded) {
        println(makeTerrain(matrixTerrain, showLegend, true, false))
        println("Choose the Target cell (e.g 2D)")
        val targetCell = readLine()
        if (targetCell == "exit") {
            return
        }
        val targetCellCoordinates = getCoordinates(targetCell)
        if (targetCellCoordinates == null) {
            print(invalidInput)
        } else if (!(isCoordinateInsideTerrain(targetCellCoordinates, numColumns, numLines))) {
            print(invalidInput)
        } else {
            val currentCoord = getCurrentPlayerCoord(matrixTerrain)
            if (!(isMovementPValid(currentCoord, targetCellCoordinates))) {
                print(invalidInput)
            } else {
                when (matrixTerrain[targetCellCoordinates.first][targetCellCoordinates.second].first) {
                    "f" -> {
                        println(makeTerrain(matrixTerrain, showLegend, true,  true))
                        println("You won the game! :)")
                        gameEnded = true
                    }
                    "*" -> {
                        println(makeTerrain(matrixTerrain, showLegend, true,  true))
                        println("You lost the game! :(")
                        gameEnded = true
                    }
                    else -> {
                        matrixTerrain[currentCoord.first][currentCoord.second] = Pair(
                            countNumberOfMinesCloseToCurrentCell(matrixTerrain,
                                currentCoord.first, currentCoord.second).toString(), true)
                        matrixTerrain[targetCellCoordinates.first][targetCellCoordinates.second] = Pair("P", true)
                        revealMatrix(matrixTerrain, targetCellCoordinates.first, targetCellCoordinates.second)
                    }
                }
            }
        }
    }
    main()
}