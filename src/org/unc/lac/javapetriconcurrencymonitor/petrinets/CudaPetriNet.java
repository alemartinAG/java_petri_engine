package org.unc.lac.javapetriconcurrencymonitor.petrinets;

import com.google.gson.Gson;
import org.unc.lac.javapetriconcurrencymonitor.exceptions.NotInitializedPetriNetException;
import org.unc.lac.javapetriconcurrencymonitor.exceptions.PetriNetException;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.PetriNetFireOutcome;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.RootPetriNet;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.components.MArc;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.components.MPlace;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.components.MTransition;

import java.util.Arrays;

public class CudaPetriNet extends RootPetriNet {

    /**
     * Makes a PetriNet Object. This is intended to be used by PetriNetFactory
     *
     * @param _places           Array of Place objects (dimension p)
     * @param _transitions      Array of Transition objects (dimension t)
     * @param _arcs             Array of Arcs
     * @param _initialMarking   Array of Integers (tokens in each place) (dimension p)
     * @param _preI             Pre-Incidence matrix (dimension p*t)
     * @param _posI             Post-Incidence matrix (dimension p*t)
     * @param _I                Incidence matrix (dimension p*t)
     * @param _inhibitionMatrix Pre-Incidence matrix for inhibition arcs only. If no inhibition arcs, null is accepted.
     * @param _resetMatrix      Pre-Incidence matrix for reset arcs only. If no reset arcs, null is accepted.
     * @param _readerMatrix     Pre-Incidence matrix for reader arcs only. If no reader arcs, null is accepted.
     */
    public CudaPetriNet(MPlace[] _places, MTransition[] _transitions, MArc[] _arcs, Integer[] _initialMarking, Integer[][] _preI, Integer[][] _posI, Integer[][] _I, Boolean[][] _inhibitionMatrix, Boolean[][] _resetMatrix, Integer[][] _readerMatrix) {
        super(_places, _transitions, _arcs, _initialMarking, _preI, _posI, _I, _inhibitionMatrix, _resetMatrix, _readerMatrix);
    }

    @Override
    protected boolean[] computeEnabledTransitions() {
        return areEnabled();
    }

    @Override
    public synchronized PetriNetFireOutcome fire(final MTransition transition) throws IllegalArgumentException, NotInitializedPetriNetException, PetriNetException {

        return PetriNetFireOutcome.SUCCESS;
    }

    /**
     * Method used to convert matrices to a json format, needed to send via http
     * @return string containing all matrices used in json format
     */
    public String matricesToJSON(){

        Gson gson = new Gson();

        Integer [][] inh = booltoInt(inhibitionMatrix);
        Integer [][] res = booltoInt(resetMatrix);

        String matrices_json = 	gson.toJson(inc) + "\n" +
                gson.toJson(inh) + "\n" +
                gson.toJson(readerMatrix) +"\n" +
                gson.toJson(res) +"\n" +
                gson.toJson(currentMarking);


        return matrices_json;
    }

    /**
     * Converts a boolean matrix into an integer matrix containing 1s or 0s
     * @param mat matrix to be converted
     * @return integer matrix calculated
     */
    private Integer[][] booltoInt(Boolean[][] mat){

        Integer [][] mat_int = new Integer[mat.length][mat[0].length];

        for(int i = 0; i<mat.length; i++){
            for(int j = 0; j<mat[0].length; j++){
                if(mat[i][j]){
                    mat_int[i][j] = 1;
                }
                else{
                    mat_int[i][j] = 0;
                }
            }
        }

        return mat_int;

    }
}
