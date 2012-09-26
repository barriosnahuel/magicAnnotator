/*
 * Copyright (c) 2012 Nahuel Barrios <barrios.nahuel@gmail.com>.
 * No se reconocerá ningún tipo de garantía.
 */

package com.nbempire.android.magicannotator.activity;

import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import com.nbempire.android.magicannotator.AppParameter;
import com.nbempire.android.magicannotator.R;
import com.nbempire.android.magicannotator.domain.Player;
import com.nbempire.android.magicannotator.domain.game.Chancho;
import com.nbempire.android.magicannotator.domain.game.Game;
import com.nbempire.android.magicannotator.util.ArrayUtil;
import com.nbempire.android.magicannotator.util.android.TableListAdapter;

/**
 * {@link Activity} para anotar un partido de {@link Chancho}.
 *
 * @author Nahuel Barrios.
 */
public class ChanchoAnnotatorActivity extends Activity {

    private static final String CHANCHO = "CHANCHO";

    /**
     * El {@link Game} que contiene los usuarios.
     */
    private Game aGame;

    /**
     * {@link Bundle} con los puntajes de cada jugador.
     */
    private Bundle scores;

    /**
     * {@link String} key para trabajar con el {@link Bundle} para guardar la instancia de la {@link Activity}.
     */
    private static final String SCORES_BUNDLE_KEY = "scores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chanchoannotator);

        aGame = (Game) this.getIntent().getExtras().getSerializable(AppParameter.GAME);

        GridView grid = ((GridView) findViewById(R.id.chanchoAnnotator_playersGridView));

        boolean forUpdate = false;
        if (savedInstanceState != null) {
            scores = savedInstanceState.getBundle(SCORES_BUNDLE_KEY);
            forUpdate = true;
        } else {
            scores = new Bundle();
        }

        updateGrid(grid, forUpdate);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle(SCORES_BUNDLE_KEY, scores);
    }

    /**
     * Actualiza la {@link GridView} que le pasamos como parámetro (en la que se ven los jugadores y sus puntajes), en base a los jugadores
     * del atributo de clase {@link Game}. En base al parámetro forUpdate asignará el puntaje por default cuando se cree por primera vez la
     * grilla, o utilizará los scores guardados.
     *
     * @param gridView
     *         {@link GridView} que se va a actualizar.
     * @param forUpdate
     *         {@link Boolean} indicando en <code>true</code> que la grilla es la primera vez que se completa. <code>false</code> cuando sea
     *         una actualización.
     *
     * @author Nahuel Barrios.
     */
    private void updateGrid(GridView gridView, boolean forUpdate) {
        if (!forUpdate) {
            for (Player player : aGame.getTeams().get(0).getPlayers()) {
                scores.putString(player.getNickName(), "- - - - - - -");
            }
        }

        gridView.setAdapter(new TableListAdapter(this, this.getValuesForGrid(scores), 2));
    }

    /**
     * Prepara la lista necesaria de nombre de jugadores-puntajes para utilizar un {@link TableListAdapter}.
     *
     * @param playersScores
     *         {@link Bundle} con pares nombre del jugador-puntaje de los cuáles se va a armar el {@link List} final.
     *
     * @return {@link List} de {@link CharSequence} con el resultado final.
     *
     * @author Nahuel Barrios.
     */
    private List<CharSequence> getValuesForGrid(Bundle playersScores) {
        List<CharSequence> result = new ArrayList<CharSequence>();
        result.add(this.getText(R.string.player).toString());
        result.add(this.getText(R.string.tutePartialResults_lostHands));

        SortedSet<String> sortedSet = new TreeSet<String>();
        for (String eachPlayerNickname : playersScores.keySet()) {
            sortedSet.add(eachPlayerNickname);
        }

        Iterator<String> iterator = sortedSet.iterator();
        while (iterator.hasNext()) {
            String eachKey = (String) iterator.next();
            result.add(eachKey);
            result.add(playersScores.getString(eachKey));
        }

        return result;
    }

    /**
     * Método ejecutado desde el layout para abrir el selector de un jugador al que se le va a actualizar el puntaje.
     *
     * @param view
     *         {@link View} la vista que llamó al método.
     *
     * @author Nahuel Barrios.
     */
    public void openPlayersSelector(View view) {
        AlertDialog.Builder selectPlayerDialog = new AlertDialog.Builder(view.getContext());

        final List<String> possibleLoosers = new ArrayList<String>();
        Iterator<String> iterator = scores.keySet().iterator();
        while (iterator.hasNext()) {
            String playerNickname = iterator.next();
            if (!scores.get(playerNickname).equals(CHANCHO)) {
                possibleLoosers.add(playerNickname);
            }
        }
        Collections.sort(possibleLoosers);
        selectPlayerDialog.setTitle(this.getText(R.string.chooseLooser));
        selectPlayerDialog.setSingleChoiceItems(ArrayUtil.toArray(possibleLoosers), -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int position) {
                String playerNickname = possibleLoosers.get(position);
                boolean lost = updateScore(playerNickname);
                dialog.dismiss();
                if (lost) {
                    Toast.makeText(getApplicationContext(), playerNickname + " " + getText(R.string.commonLabel_someoneLost),
                                          Toast.LENGTH_LONG).show();
                }
            }

        });
        selectPlayerDialog.show();
    }

    /**
     * Actualiza el puntaje para un jugador dado en base al parámetro.
     *
     * @param playerNickname
     *         {@link String} con el nickname del jugador al que se le va a actualizar el puntaje.
     *
     * @return <code>true</code> cuando el jugador alcancó el puntaje máximo. <code>false</code> cuando puede seguir perdiendo manos.
     *
     * @author Nahuel Barrios.
     */
    private boolean updateScore(String playerNickname) {
        String currentScore = scores.getString(playerNickname);

        String nextScore = "";

        if (currentScore.length() < CHANCHO.length()) {
            nextScore = currentScore;
        }

        char[] characters = CHANCHO.toCharArray();
        for (int counter = 0; counter < characters.length; counter++) {
            char eachCharacter = characters[counter];
            try {
                if (nextScore.charAt(counter) != eachCharacter) {
                    break;
                }
            } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                nextScore += String.valueOf(eachCharacter);
                break;
            }
        }

        scores.putString(playerNickname, nextScore);
        this.updateGrid(((GridView) findViewById(R.id.chanchoAnnotator_playersGridView)), true);
        return this.perdioCompletamente(nextScore);
    }

    /**
     * TODO : JavaDoc : for ChanchoAnnotatorActivity.perdioCompletamente().
     *
     * @param nextScore
     *
     * @return {@link boolean}
     *
     * @author Nahuel Barrios.
     */
    private boolean perdioCompletamente(String nextScore) {
        return nextScore.equals(CHANCHO);
    }
}
