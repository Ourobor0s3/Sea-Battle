import Entities.Enum.StatusCell
import Entities.Model.GameBoard
import Entities.Model.ModelPlayer
import Players.PlayerComputer
import Players.PlayerHuman

class Game {
    private val playerHuman = PlayerHuman(GameBoard())
    private val playerComputer = PlayerComputer(GameBoard())
    private var isGameOver = false

    fun start() {
        println("Добро пожаловать в Морской бой!")

        playerHuman.placeShips()
        playerComputer.placeShips()

        while (!isGameOver) {
            playTurn(playerHuman, playerComputer, isHumanTurn = true)
            if (isGameOver) break
            playTurn(playerComputer, playerHuman, isHumanTurn = false)
        }
        println("Игра окончена!")
    }

    private fun playTurn(currentPlayer: ModelPlayer, opponentPlayer: ModelPlayer, isHumanTurn: Boolean) {
        var continueTurn = true
        while (continueTurn && !isGameOver) {
            if (isHumanTurn) {
                printBoards()
            }

            val move = currentPlayer.makeMove(opponentPlayer.gameBoard)
            if (!opponentPlayer.gameBoard.isValidMove(move)) {
                println("Неверный ход. Попробуйте снова.")
                continue
            }

            val result = opponentPlayer.gameBoard.receiveShot(move)

            currentPlayer.processShotResult(opponentPlayer.gameBoard, move, result)
            continueTurn = result == StatusCell.HIT

            checkGameOver(opponentPlayer)
        }
    }

    private fun checkGameOver(modelPlayer: ModelPlayer) {
        isGameOver = modelPlayer.gameBoard.grid.flatten().none { it.status == StatusCell.SHIP }
        if (isGameOver) {
            println("${modelPlayer::class.simpleName} проиграл! Все корабли потоплены.")
        }
    }

    // Вывод досок игрока и компьютера
    private fun printBoards() {
        print("\tВаша доска:\t\t\t\tДоска компьютера:\n   ")
        for (i in 0 until playerHuman.gameBoard.size) {
            print("$i ")
        }
        print("\t\t   ")
        for (i in 0 until playerComputer.gameBoard.size) {
            print("$i ")
        }

        for (i in 0 until playerHuman.gameBoard.size) {
            print("\n$i  ")
            printRow(playerHuman.gameBoard, i, true)
            print("\t\t$i  ")
            printRow(playerComputer.gameBoard, i, false)
        }
        println()
    }

    // Вывод строки доски
    private fun printRow(gameBoard: GameBoard, row: Int, showShips: Boolean) {
        for (cell in gameBoard.grid[row]) { print("${cell.getStat(showShips)} ") }
    }
}
