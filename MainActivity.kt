package com.example.mukul_minesweeper

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var level = ""
    var highScore = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonCBMaker.setOnClickListener{
            level=""
            customTextVisibility(buttonCBMaker)
        }
        easyRadio.setOnClickListener {
            level="easy"
            customTextVisibility(easyRadio)
        }
        mediumRadio.setOnClickListener{
            level="medium"
            customTextVisibility(mediumRadio)
        }
        hardRadio.setOnClickListener{
            level="hard"
            customTextVisibility(hardRadio)
        }
        gamestartButton.setOnClickListener{
            startGame(level)
        }
        gameinfoButton.setOnClickListener{
            showInstructions()
        }
        shareBestTimeButton.setOnClickListener{
            shareHighScore()
        }

    }

    private fun shareHighScore() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my highscore in minesweeper game : "+highScore)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // To show basic instructions
    private fun showInstructions() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("INSTRUCTIONS")
        builder.setMessage("I hope you are familiar with the game rules. Here are some app functionalities that will help you get through\n"+
        "1. You can either select from the given levels or can create a custom board according to your requirements\n" +
                "2. You can use the share button to share your score with friends\n"+
                "3. Start button will start the game and the timer will get started on first click\n" +
                "4. You can keep a track of marked mines using mine count\n" +
                "5. You can toggle between flag/mine using the button on top to either flag or open the mine respectively\n" +
                "6. Smile icon button can be used to refresh the game\n" +
                "Have Fun!")

        builder.setCancelable(false)

        builder.setPositiveButton("OK"
        ){ dialog, which ->

        }

        val alertDialog = builder.create()
        alertDialog.show()

    }

    // On pressing back button
    override fun onBackPressed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setMessage("ARE YOU SURE YOU WANT TO EXIT THE APP?")
        builder.setTitle("ALERT!!")
        builder.setCancelable(false)

        builder.setPositiveButton("YES"
        ){ dialog, which ->
            val exitAppIntent = Intent(Intent.ACTION_MAIN)
            exitAppIntent.addCategory(Intent.CATEGORY_HOME)
            exitAppIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(exitAppIntent)
            finish()
            super.onBackPressed()
        }

        builder.setNegativeButton("NO", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
            }
        })

        val alertDialog = builder.create()
        alertDialog.show()
    }


    // On resuming of activity
    override fun onResume() {
        super.onResume()

        // Clearing Radio Buttons
        selectGameLevel.clearCheck()

        val intent = intent
        if(intent.getStringExtra("lastTime") != null || intent.getStringExtra("highScore") != null ) {
            lastGameTimeOut.text = " " + intent.getStringExtra("lastTime")
            bestTimeOut.text = " " + intent.getStringExtra("highScore")
            highScore = intent.getStringExtra("highScore")!!
        }else{
            lastGameTimeOut.text = " NA"
            bestTimeOut.text = " NA"
        }
    }

    // This function will get called on clicking start button
    private fun startGame(level: String){
        if(level.equals("")){
            val rows = findViewById<EditText>(R.id.enterRowsEdit)
            val columns = findViewById<EditText>(R.id.enterColumnsEdit)
            val mines = findViewById<EditText>(R.id.enterMinesEdit)

            // Making sure the fields are not empty
            if(TextUtils.isEmpty(rows.text.toString()) || TextUtils.isEmpty(columns.text.toString()) || TextUtils.isEmpty(mines.text.toString())){
                Toast.makeText(this,"FIELDS CANNOT BE EMPTY!!",Toast.LENGTH_LONG).show()
            }
            else {
                var row = Integer.parseInt(rows.text.toString())
                var column = Integer.parseInt(columns.text.toString())
                var mine = Integer.parseInt(mines.text.toString())

                // Checking for overcrowding of rows and columns
                if(row>25 || column>25 || row<5 || column<5){
                    Toast.makeText(this,"THE NUMBER OF ROWS & COLUMNS SHOULD BETWEEN 4-25. ",Toast.LENGTH_SHORT).show()
                }
                else if(mine<3){
                    Toast.makeText(this,"THE NUMBER OF MINES SHOULD BE GREATER THAN 2. ",Toast.LENGTH_LONG).show()
                }
                //Checking for overcrowding of mines
                else if(mine > (row*column/4)){
                    Toast.makeText(this,"THE NUMBER OF MINES SHOULD BE LESS TO AVOID OVERCROWDING!! ",Toast.LENGTH_LONG).show()
                }
                // Sending row, column and mine number using intents
                else {
                    val intent = Intent(this, BoardActivity::class.java).apply {
                        putExtra("numberOfRows", row)
                        putExtra("numberOfColumns", column)
                        putExtra("numberOfMines", mine)
                        putExtra("flag", 0)
                    }
                    startActivity(intent)
                }
            }
        }
        // Sending the selected level using intents
        else{
            val intent = Intent(this, BoardActivity::class.java).apply {
                putExtra("selectedLevel",level)
                putExtra("flag",1)
            }
            startActivity(intent)
        }
    }

    // Setting visibility of textViews on the basis of level/custom board selection
    private fun customTextVisibility(view: View){
        if (view is RadioButton) {
            val checked = view.isChecked
            if(checked) {
                // Setting view visibility if radio buttons are used
                enterRowsEdit.visibility = View.INVISIBLE
                enterColumnsEdit.visibility = View.INVISIBLE
                enterMinesEdit.visibility = View.INVISIBLE
            }
        }else{
            // Clearing Radio Buttons
            selectGameLevel.clearCheck()

            // Setting view visibility
            enterRowsEdit.visibility = View.VISIBLE
            enterColumnsEdit.visibility = View.VISIBLE
            enterMinesEdit.visibility = View.VISIBLE
        }
    }
}