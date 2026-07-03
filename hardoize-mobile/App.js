/*
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  APP.JS                                                      ║
 * ║                                                              ║
 * ║  Point d'entrée principal de l'application Hardoize.         ║
 * ║                                                              ║
 * ║  Responsabilités :                                           ║
 * ║  1. Initialisation de la base de données SQLite              ║
 * ║  2. Vérification de la session existante                     ║
 * ║  3. Choix du navigateur : Auth ou App                        ║
 * ║  4. Application du thème (clair/sombre)                      ║
 * ║  5. Surveillance de la connexion réseau                      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */

import "react-native-gesture-handler";
import React, { useEffect, useState, useCallback } from "react";
import {
  View,
  Text,
  StyleSheet,
  StatusBar,
  ActivityIndicator,
  useColorScheme,
} from "react-native";
import { NavigationContainer }  from "@react-navigation/native";
import { SafeAreaProvider }     from "react-native-safe-area-context";

import AuthNavigator from "./src/navigation/AuthNavigator";
import AppNavigator  from "./src/navigation/AppNavigator";
import useStore      from "./src/store/useStore";
import { recupererSession } from "./src/services/authService";
import { ouvrirDatabase }   from "./src/services/database";
import { COLORS }           from "./src/utils/constants";
import { Appearance } from "react-native";
import { NavigationBar } from "expo-navigation-bar";

export default function App() {
  // ── État local ─────────────────────────────────────────────
  // true pendant le chargement initial
  const [chargement, setChargement] = useState(true);
  // Message d'erreur d'initialisation
  const [erreurInit, setErreurInit] = useState(null);

  // ── Store global ───────────────────────────────────────────
  const estConnecte    = useStore((s) => s.estConnecte);
  const setSession     = useStore((s) => s.setSession);
  const darkMode       = useStore((s) => s.darkMode);
  const setDarkMode    = useStore((s) => s.setDarkMode);

  // Thème système (clair/sombre selon les paramètres du téléphone)
  const colorScheme = useColorScheme();

  // ── Initialisation ─────────────────────────────────────────
  useEffect(() => {
    initialiser();
  }, []);

  // Synchroniser le thème avec les paramètres système
  useEffect(() => {
    setDarkMode(Appearance.getColorScheme() === "dark");

    const subscription = Appearance.addChangeListener(({colorScheme})  => {
      setDarkMode(colorScheme === "dark");
    });
      return () => subscription.remove();
  }, []);

  /**
   * Initialisation de l'application au démarrage.
   *
   * Étapes :
   * 1. Ouvrir et créer les tables SQLite
   * 2. Vérifier si une session existe (JWT valide)
   * 3. Restaurer la session dans le store si connecté
   */
  const initialiser = async () => {
    try {
      setChargement(true);

      // ─ Étape 1 : Initialiser la base de données locale ──────
      console.log("🔄 Initialisation de la base de données...");
      await ouvrirDatabase();
      console.log("✅ Base de données prête");

      // ─ Étape 2 : Vérifier la session existante ──────────────
      console.log("🔄 Vérification de la session...");
      const session = await recupererSession();

      if (session) {
        // Session valide → restaurer dans le store
        console.log("✅ Session restaurée :", session.utilisateur.nom);
        setSession(
          session.utilisateur,
          session.token,
          session.refreshToken || null
        );
      } else {
        console.log("ℹ Aucune session → écran de connexion");
      }

    } catch (error) {
      console.error("❌ Erreur initialisation :", error);
      setErreurInit(error.message);
    } finally {
      setChargement(false);
    }
  };

  // ── Couleurs du thème actif ────────────────────────────────
  const theme = {
    background: darkMode ? COLORS.BG_DARK     : COLORS.BG_LIGHT,
    surface:    darkMode ? COLORS.SURFACE_DARK : COLORS.SURFACE_LIGHT,
    text:       darkMode ? COLORS.TEXT_PRIMARY_DARK : COLORS.TEXT_PRIMARY_LIGHT,
  };

  // ── Écran de chargement ────────────────────────────────────
  if (chargement) {
    return (
      <View
        style={[
          styles.loading,
          { backgroundColor: COLORS.BG_DARK },
        ]}
      >
        <StatusBar barStyle="light-content" backgroundColor={COLORS.BG_DARK} />

        {/* Logo Hardoize */}
        <View style={styles.logoContainer}>
          <Text style={styles.logoLettre}>H</Text>
        </View>

        <Text style={styles.logoTexte}>Hardoize</Text>
        <Text style={styles.logoSousTexte}>
          Gérez vos ventes en toute simplicité.
        </Text>

        {/* Indicateur de chargement */}
        <ActivityIndicator
          size="large"
          color={COLORS.ORANGE}
          style={styles.spinner}
        />
      </View>
    );
  }

  // ── Écran d'erreur d'initialisation ───────────────────────
  if (erreurInit) {
    return (
      <View style={[styles.loading, { backgroundColor: COLORS.BG_DARK }]}>
        <Text style={styles.erreurTexte}>⚠ Erreur de démarrage</Text>
        <Text style={styles.erreurDetail}>{erreurInit}</Text>
        <Text
          style={styles.retry}
          onPress={initialiser}
        >
          Réessayer
        </Text>
      </View>
    );
  }

  // ── Application principale ─────────────────────────────────
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        {/* StatusBar adapté au thème */}
        <StatusBar
          barStyle={darkMode ? "light-content" : "dark-content"}
          backgroundColor={theme.background}
        />
        <NavigationBar hidden={true}/>
        {/*
         * Choix du navigateur selon l'état de connexion :
         * - Non connecté → AuthNavigator (Login + Register)
         * - Connecté     → AppNavigator  (Bottom Tabs)
         */}
        {estConnecte ? <AppNavigator /> : <AuthNavigator />}

      </NavigationContainer>
    </SafeAreaProvider>
  );
}

// ── Styles ─────────────────────────────────────────────────────
const styles = StyleSheet.create({
  // Écran de chargement / splash
  loading: {
    flex:           1,
    alignItems:     "center",
    justifyContent: "center",
    paddingHorizontal: 32,
  },

  // Cercle logo orange
  logoContainer: {
    width:           80,
    height:          80,
    borderRadius:    20,
    backgroundColor: COLORS.ORANGE,
    alignItems:      "center",
    justifyContent:  "center",
    marginBottom:    16,
    // Ombre
    shadowColor:     COLORS.ORANGE,
    shadowOffset:    { width: 0, height: 8 },
    shadowOpacity:   0.4,
    shadowRadius:    16,
    elevation:       8,
  },

  // Lettre H dans le logo
  logoLettre: {
    fontSize:   36,
    fontWeight: "900",
    color:      COLORS.WHITE,
  },

  // Nom de l'application
  logoTexte: {
    fontSize:   28,
    fontWeight: "800",
    color:      COLORS.WHITE,
    marginBottom: 8,
  },

  // Sous-titre
  logoSousTexte: {
    fontSize:  14,
    color:     COLORS.TEXT_SECONDARY_DARK,
    textAlign: "center",
    marginBottom: 48,
  },

  // Spinner de chargement
  spinner: {
    marginTop: 32,
  },

  // Texte erreur
  erreurTexte: {
    fontSize:   20,
    fontWeight: "700",
    color:      COLORS.ERROR,
    marginBottom: 12,
  },

  // Détail erreur
  erreurDetail: {
    fontSize:  14,
    color:     COLORS.TEXT_SECONDARY_DARK,
    textAlign: "center",
    marginBottom: 24,
  },

  // Bouton retry
  retry: {
    fontSize:        16,
    fontWeight:      "700",
    color:           COLORS.ORANGE,
    paddingVertical: 12,
    paddingHorizontal: 32,
    borderWidth:     2,
    borderColor:     COLORS.ORANGE,
    borderRadius:    12,
  },
});