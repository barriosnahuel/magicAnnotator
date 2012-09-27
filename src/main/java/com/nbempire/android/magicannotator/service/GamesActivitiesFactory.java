/*
 * Copyright (c) 2012 Nahuel Barrios <barrios.nahuel@gmail.com>.
 * No se reconocerá ningún tipo de garantía.
 */

package com.nbempire.android.magicannotator.service;

import android.app.Activity;
import com.nbempire.android.magicannotator.GameKeys;
import com.nbempire.android.magicannotator.R;
import com.nbempire.android.magicannotator.activity.GenericAnnotatorActivity;

/**
 * TODO : JavaDoc : for GamesActivitiesFactory.
 *
 * @author Nahuel Barrios.
 */
public abstract class GamesActivitiesFactory {

    /**
     * TODO : JavaDoc : for GamesActivitiesFactory.getInstance().
     *
     * @param gameName
     *
     * @return {@link Integer} -1 if there is no game that matchs the current gameName
     *
     * @author Nahuel Barrios.
     */
    public static int getGameKey(String gameName) {
        if (gameName.equals(GameKeys.GAME_NAME_OTRO)) {
            return R.string.gamename_otro;
        } else if (gameName.equals(GameKeys.GAME_NAME_MARKET)) {
            return R.string.gamename_market;
        }
        return -1;
    }

    /**
     * TODO : JavaDoc : for GamesActivitiesFactory.getAnnotator().
     *
     * @param gameKey
     *
     * @return {@link Class<? extends Activity>}
     *
     * @throws IllegalArgumentException
     * @author Nahuel Barrios.
     */
    public static Class<? extends Activity> getAnnotator(int gameKey) throws IllegalArgumentException {
        switch (gameKey) {
            case R.string.gamename_otro:
                return GenericAnnotatorActivity.class;
            default:
                throw new IllegalArgumentException("La actividad todavía no existe o no esta configurada correctamente.");
        }
    }

}
