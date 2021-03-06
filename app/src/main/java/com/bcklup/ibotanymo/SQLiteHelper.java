package com.bcklup.ibotanymo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.bcklup.ibotanymo.problems.Problem;
import com.bcklup.ibotanymo.solutions.Solution;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by gians on 10/02/2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper{

    private static String DB_PATH = "/data/data/com.bcklup.ibotanymo/databases/";

    private static String DB_NAME = "PlantDB.sqlite";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    public SQLiteHelper(Context context) {
        super(context,DB_NAME,null,1);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    public void insertPlant(String plantName, Integer plantType, Integer storeType, String plantGuide, Integer plantKind){
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO plants VALUES (NULL, ?, ?, ?, ?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();

            statement.bindString(1, plantName);
            statement.bindLong(2, plantType);
            statement.bindLong(3, storeType);
            statement.bindString(5, plantGuide);
            statement.bindLong(6, plantKind);


        statement.executeInsert();
    }
    public void insertPlanner(Long plantid){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO planner VALUES (NULL, ?, datetime())";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindLong(1, plantid);
        statement.executeInsert();
    }

    public void insertProblem(String problem, String solution){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO solutions VALUES (NULL, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, solution);

        long solid = statement.executeInsert();

        sql = "INSERT INTO problems VALUES (NULL, ?)";
        statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, problem);

        long probid = statement.executeInsert();

        sql = "INSERT INTO problems_solutions VALUES (NULL, ?, ?)";
        statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindLong(1, probid);
        statement.bindLong(2, solid);

        statement.executeInsert();

    }
    public void insertProblemExisting(String problem, int solution){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO problems VALUES (NULL, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, problem);

        long probid = statement.executeInsert();

        sql = "INSERT INTO problems_solutions VALUES (NULL, ?, ?)";
        statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindLong(1, probid);
        statement.bindLong(2, solution);

        statement.executeInsert();
    }
    public final void deletePlanner(Long plantid){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM planner WHERE _id=?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1, plantid);
        statement.executeUpdateDelete();
    }

    public final void deleteProblemAndSolution(Long problemId) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM problems WHERE _id =?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1, problemId);
        statement.executeUpdateDelete();
    }

    public final void unlinkSolutionToProblem(int problemId, int solutionId) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM problems_solutions WHERE problem_id = ? AND solution_id = ?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1, problemId);
        statement.bindLong(2, solutionId);
        statement.executeUpdateDelete();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }


    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void saveSolution(Solution solution) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE solutions SET solution = ? WHERE _id = ?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, solution.getSolution());
        statement.bindLong(2, solution.getId());
        statement.executeUpdateDelete();
    }

    public void saveProblem(Problem problem) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "UPDATE problems SET problem = ? WHERE _id  = ?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, problem.problem);
        statement.bindLong(2, problem.id);
        statement.executeUpdateDelete();
    }

    public void addSolutionToExistingProblem(Problem problem, Solution solution) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO problems_solutions VALUES (NULL, ?, ?)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindLong(1, problem.id);
        statement.bindLong(2, solution.getId());
        statement.executeInsert();
    }

    public void addNewSolutionToProblem(Problem problem, Solution solution) {
        SQLiteDatabase db = getWritableDatabase();
        String insertSolutionSql = "INSERT INTO solutions VALUES (NULL, ?)";
        SQLiteStatement statement = db.compileStatement(insertSolutionSql);
        statement.bindString(1, solution.getSolution());

        long solutionId = statement.executeInsert();

        String relationSql = "INSERT INTO problems_solutions VALUES (NULL, ?, ?)";
        SQLiteStatement relationalStatement = db.compileStatement(relationSql);
        relationalStatement.bindLong(1, problem.id);
        relationalStatement.bindLong(2, solutionId);

        relationalStatement.executeInsert();
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }
}
