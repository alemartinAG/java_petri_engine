package org.unc.lac.javapetriconcurrencymonitor.utils;

import java.util.Arrays;
import java.util.HashSet;

public class MatrixUtils {

    public static Object[][] transpose(Object[][] matrix){

        int rows = matrix[0].length;
        int columns = matrix.length;

        Object[][] matrix_T = new Object[rows][columns];

        for(int i=0; i<columns; i++){
            for(int j=0; j<rows; j++){
                matrix_T[j][i] = matrix[i][j];
            }
        }

        return matrix_T;

    }

    public static boolean[] columnsNotZero(Integer[][] matrix){

        boolean [] result = new boolean[matrix[0].length];
        Arrays.fill(result,false);

        for(int i = 0; i<matrix[0].length;i++){
            for(int j=0; j<matrix.length; j++){
                if (matrix[j][i] != 0) {
                    result[i] = true;
                    break;
                }
            }
        }
        return result;
    }

    public static boolean[] columnsNotZero(Boolean[][] matrix){

        boolean [] result = new boolean[matrix[0].length];
        Arrays.fill(result,false);

        for(int i=0; i<matrix[0].length;i++){
            for(int j=0; j<matrix.length; j++){
                if (matrix[j][i]) {
                    result[i] = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Checks if all elements in the matrix are false.
     * This is used to know if the petri has the type of arcs described by the matrix semantics.
     */
    public static boolean isMatrixNonZero(Boolean[][] matrix){

        try{

            int rows = matrix.length;
            int columns = matrix[0].length;

            Boolean[] zero = new Boolean[columns];
            Arrays.fill(zero, false);

            for(int i=0; i<rows; i++){
                if(!Arrays.equals(matrix[i], zero)){
                    return false;
                }
            }

            return true;

        } catch (NullPointerException e){
            return false;
        }
    }

    /**
     * Checks if all elements in the matrix are zero.
     * This is used to know if the petri has the type of arcs described by the matrix semantics.
     */
    public static boolean isMatrixNonZero(Integer[][] matrix){

        try{

            int rows = matrix.length;
            int columns = matrix[0].length;

            Integer[] zero = new Integer[columns];
            Arrays.fill(zero, 0);

            for(int i=0; i<rows; i++){
                if(!Arrays.equals(matrix[i], zero)){
                    return false;
                }
            }

            return true;

        } catch (NullPointerException e){
            return false;
        }

    }

}
