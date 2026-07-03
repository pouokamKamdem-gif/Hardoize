/*
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  VENTESSCREEN.JS                                             ║
 * ║                                                              ║
 * ║  Écran principal de vente Hardoize.                          ║
 * ║                                                              ║
 * ║  Fonctionnalités :                                           ║
 * ║  - Toggle Produits / Historique                              ║
 * ║  - Grille produits avec photo en fond + overlay lisible      ║
 * ║  - Tap = ajoute une ligne dans le panier (multi-lignes)      ║
 * ║  - Boutons +/- par ligne de panier                           ║
 * ║  - Recherche/création client rapide pour vente espèces/créd. ║
 * ║  - Historique complet trié (aujourd'hui, hier, vendeur...)   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */

import React, { useState, useEffect, useCallback } from "react";
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  TextInput,
  Alert,
  Modal,
  ScrollView,
  StatusBar,
  RefreshControl,
  Dimensions,
  ImageBackground,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useFocusEffect } from "@react-navigation/native";

import useStore  from "../../store/useStore";
import { COLORS, PAIEMENT, MOTIF_MOUVEMENT, MOUVEMENT, ROLES } from "../../utils/constants";
import { formatFCFA, formatDateHeure } from "../../utils/helpers";
import {
  ProduitDB, VenteDB, DetteDB, ClientDB,
  MouvementDB, HistoriqueDB,
} from "../../services/database";
import Button from "../../components/common/Button";
import Input  from "../../components/common/Input";

const { width } = Dimensions.get("window");
const CARD_WIDTH = (width - 48) / 2;

const VentesScreen = () => {
  // ── Store global ───────────────────────────────────────────
  const utilisateur     = useStore((s) => s.utilisateur);
  const groupeActif     = useStore((s) => s.groupeActif);
  const panier          = useStore((s) => s.panier);
  const ajouterAuPanier = useStore((s) => s.ajouterAuPanier);
  const viderPanier     = useStore((s) => s.viderPanier);
  const getTotalPanier  = useStore((s) => s.getTotalPanier);
  const getNbArticles   = useStore((s) => s.getNbArticles);
  const darkMode        = useStore((s) => s.darkMode);

  const groupeId = groupeActif?.id || 0;

  // ── Couleurs thème ─────────────────────────────────────────
  const bgCouleur      = darkMode ? COLORS.BG_DARK     : COLORS.BG_LIGHT;
  const cardCouleur    = darkMode ? COLORS.SURFACE_DARK : COLORS.SURFACE_LIGHT;
  const textCouleur    = darkMode ? COLORS.TEXT_PRIMARY_DARK   : COLORS.TEXT_PRIMARY_LIGHT;
  const textSecondaire = darkMode ? COLORS.TEXT_SECONDARY_DARK : COLORS.TEXT_SECONDARY_LIGHT;
  const inputBg        = darkMode ? COLORS.SURFACE_DARK : COLORS.BG_CARD_LIGHT;

  // ── Onglet actif : "produits" | "historique" ───────────────
  const [onglet, setOnglet] = useState("produits");

  // ── État Produits ──────────────────────────────────────────
  const [produits,   setProduits]   = useState([]);
  const [recherche,  setRecherche]  = useState("");
  const [chargement, setChargement] = useState(false);
  const [totalJour,  setTotalJour]  = useState(0);

  // ── État Historique ────────────────────────────────────────
  const [historique,      setHistorique]      = useState([]);
  const [filtreHisto,     setFiltreHisto]      = useState("tout");
  const [modalDetailVente, setModalDetailVente] = useState(false);
  const [venteDetail,      setVenteDetail]      = useState(null);

  // ── Modal paiement (espèces ou crédit) ─────────────────────
  const [modalPaiement,  setModalPaiement]  = useState(false);
  const [typePaiementEnCours, setTypePaiementEnCours] = useState(null);
  const [rechercheClient, setRechercheClient] = useState("");
  const [clientsTrouves,  setClientsTrouves]  = useState([]);
  const [clientChoisi,    setClientChoisi]    = useState(null);
  const [modeNouveauCli,  setModeNouveauCli]  = useState(false);
  const [nomNouveauCli,   setNomNouveauCli]   = useState("");
  const [telNouveauCli,   setTelNouveauCli]   = useState("");
  const [dateRemb,        setDateRemb]        = useState("");

  // ── Charger au focus ───────────────────────────────────────
  useFocusEffect(
    useCallback(() => {
      if (onglet === "produits") {
        chargerProduits();
        chargerTotalJour();
      } else {
        chargerHistorique();
      }
    }, [onglet, recherche, groupeId, filtreHisto])
  );

  // ── Charger produits ───────────────────────────────────────
  const chargerProduits = async () => {
    try {
      setChargement(true);
      const liste = recherche.trim()
        ? await ProduitDB.rechercher(groupeId, recherche.trim())
        : await ProduitDB.getByGroupe(groupeId);
      setProduits(liste);
    } catch (error) {
      console.error("Erreur chargement produits:", error);
    } finally {
      setChargement(false);
    }
  };

  const chargerTotalJour = async () => {
    try {
      const total = await VenteDB.getTotalJour(groupeId);
      setTotalJour(total || 0);
    } catch (error) {
      console.error("Erreur total jour:", error);
    }
  };

  // ── Charger historique ─────────────────────────────────────
  const chargerHistorique = async () => {
    try {
      setChargement(true);
      const liste = await HistoriqueDB.getFiltre(groupeId, filtreHisto);
      setHistorique(liste);
    } catch (error) {
      console.error("Erreur chargement historique:", error);
    } finally {
      setChargement(false);
    }
  };

  // ── Tap sur un produit = ajouter au panier ─────────────────
  const handleTapProduit = (produit) => {
    const ligneExistante = panier.find((l) => l.produit.id === produit.id);
    const qteActuelle    = ligneExistante?.quantite || 0;

    if (qteActuelle >= produit.quantiteStock) {
      Alert.alert(
        "Stock insuffisant",
        `Il ne reste que ${produit.quantiteStock} unité(s) de ${produit.nom}`
      );
      return;
    }
    ajouterAuPanier(produit);
  };

  // ── +/- sur une ligne du panier ─────────────────────────────
  const incrementerLigne = (produit) => {
    if (panier.find((l) => l.produit.id === produit.id)?.quantite >= produit.quantiteStock) {
      Alert.alert("Stock insuffisant", `Maximum ${produit.quantiteStock} unité(s)`);
      return;
    }
    ajouterAuPanier(produit);
  };

  const decrementerLigne = (produitId) => {
    // On manipule directement via le store : on retire une unité
    const ligne = panier.find((l) => l.produit.id === produitId);
    if (!ligne) return;

    if (ligne.quantite <= 1) {
      // Retirer complètement la ligne
      const nouveauPanier = panier.filter((l) => l.produit.id !== produitId);
      useStore.setState({ panier: nouveauPanier });
    } else {
      const nouveauPanier = panier.map((l) =>
        l.produit.id === produitId
          ? { ...l, quantite: l.quantite - 1, sousTotal: (l.quantite - 1) * l.produit.prixVente }
          : l
      );
      useStore.setState({ panier: nouveauPanier });
    }
  };

  // ── Ouvrir le modal de paiement (espèces ou crédit) ────────
  const ouvrirModalPaiement = async (type) => {
    setTypePaiementEnCours(type);
    setClientChoisi(null);
    setModeNouveauCli(false);
    setRechercheClient("");
    setNomNouveauCli("");
    setTelNouveauCli("");
    setDateRemb("");

    // Charger tous les clients pour la recherche rapide
    try {
      const clients = await ClientDB.getByGroupe(groupeId);
      setClientsTrouves(clients);
    } catch {
      setClientsTrouves([]);
    }

    setModalPaiement(true);
  };

  // ── Recherche client en temps réel (nom + téléphone) ───────
  const handleRechercheClient = async (texte) => {
    setRechercheClient(texte);
    try {
      const clients = texte.trim()
        ? await ClientDB.rechercher(groupeId, texte.trim())
        : await ClientDB.getByGroupe(groupeId);
      setClientsTrouves(clients);
    } catch (error) {
      console.error("Erreur recherche client:", error);
    }
  };

  // ── Confirmer le paiement (espèces : client optionnel) ─────
  const confirmerPaiement = async () => {
    if (typePaiementEnCours === PAIEMENT.CREDIT) {
      // Crédit : client obligatoire (existant ou nouveau)
      if (!clientChoisi && !modeNouveauCli) {
        Alert.alert("Erreur", "Choisissez un client ou créez-en un nouveau");
        return;
      }
      if (modeNouveauCli && (!nomNouveauCli.trim() || !telNouveauCli.trim())) {
        Alert.alert("Erreur", "Nom et téléphone du client obligatoires");
        return;
      }
      if (!dateRemb.trim()) {
        Alert.alert("Erreur", "Date de remboursement obligatoire (JJ/MM/AAAA)");
        return;
      }
    }

    try {
      let clientId = null;
      let nomClientFinal = null;

      if (typePaiementEnCours === PAIEMENT.CREDIT) {
        if (modeNouveauCli) {
          const existant = await ClientDB.getByTelephone(telNouveauCli.trim(), groupeId);
          if (existant) {
            clientId = existant.id;
            nomClientFinal = existant.nomClient;
          } else {
            clientId = await ClientDB.inserer({
              nomClient:     nomNouveauCli.trim(),
              numeroClient:  telNouveauCli.trim(),
              groupeId,
              utilisateurId: utilisateur?.id || null,
            });
            nomClientFinal = nomNouveauCli.trim();
          }
        } else {
          clientId = clientChoisi.id;
          nomClientFinal = clientChoisi.nomClient;
        }
      } else if (clientChoisi) {
        // Espèces avec client renseigné (optionnel)
        clientId = clientChoisi.id;
        nomClientFinal = clientChoisi.nomClient;
      } else if (modeNouveauCli && nomNouveauCli.trim() && telNouveauCli.trim()) {
        // Espèces avec nouveau client renseigné (optionnel)
        const existant = await ClientDB.getByTelephone(telNouveauCli.trim(), groupeId);
        if (existant) {
          clientId = existant.id;
          nomClientFinal = existant.nomClient;
        } else {
          clientId = await ClientDB.inserer({
            nomClient:     nomNouveauCli.trim(),
            numeroClient:  telNouveauCli.trim(),
            groupeId,
            utilisateurId: utilisateur?.id || null,
          });
          nomClientFinal = nomNouveauCli.trim();
        }
      }

      let dateTimestamp = null;
      if (typePaiementEnCours === PAIEMENT.CREDIT) {
        const parties = dateRemb.split("/");
        if (parties.length !== 3) {
          Alert.alert("Erreur", "Format de date invalide. Utilisez JJ/MM/AAAA");
          return;
        }
        dateTimestamp = new Date(
          parseInt(parties[2]), parseInt(parties[1]) - 1, parseInt(parties[0]),
          23, 59, 59
        ).getTime();
        if (isNaN(dateTimestamp) || dateTimestamp < Date.now()) {
          Alert.alert("Erreur", "La date doit être dans le futur");
          return;
        }
      }

      await enregistrerVentes(typePaiementEnCours, clientId, nomClientFinal, dateTimestamp);
      setModalPaiement(false);

    } catch (error) {
      Alert.alert("Erreur", error.message || "Erreur lors du paiement");
    }
  };

  // ── Enregistrer les ventes (panier complet) ────────────────
  const enregistrerVentes = async (typePaiement, clientId, nomClient, dateRemboursement) => {
    if (panier.length === 0) return;

    try {
      setChargement(true);
      const panierSnapshot = [...panier]; // copie avant de vider

      for (const ligne of panierSnapshot) {
        const { produit, quantite } = ligne;
        const montant = produit.prixVente * quantite;

        const venteId = await VenteDB.inserer({
          produitId:     produit.id,
          nomProduit:    produit.nom,
          quantite,
          prixUnitaire:  produit.prixVente,
          montantTotal:  montant,
          typePaiement,
          clientId:      clientId || null,
          utilisateurId: utilisateur?.id || null,
          groupeId,
        });

        await ProduitDB.decrementerStock(produit.id, quantite);

        await MouvementDB.inserer({
          produitId:     produit.id,
          nomProduit:    produit.nom,
          type:          MOUVEMENT.SORTIE,
          motif:         MOTIF_MOUVEMENT.VENTE,
          quantite,
          prixUnitaire:  produit.prixVente,
          montantTotal:  montant,
          utilisateurId: utilisateur?.id || null,
          groupeId,
        });

        if (typePaiement === PAIEMENT.CREDIT && clientId && dateRemboursement) {
          await DetteDB.inserer({
            clientId,
            venteId,
            montantTotal:      montant,
            dateRemboursement,
            utilisateurId:     utilisateur?.id || null,
            groupeId,
          });
        }
      }

      // Enregistrer la transaction complète dans l'historique
      await HistoriqueDB.inserer({
        panier:       panierSnapshot,
        typePaiement,
        montantTotal: panierSnapshot.reduce((t, l) => t + l.sousTotal, 0),
        clientId:     clientId || null,
        nomClient:    nomClient || null,
        vendeurId:    utilisateur?.id || null,
        nomVendeur:   utilisateur?.nom || null,
        groupeId,
      });

      viderPanier();
      await chargerProduits();
      await chargerTotalJour();

      Alert.alert(
        "✅ Vente enregistrée !",
        typePaiement === PAIEMENT.CREDIT
          ? "La dette a été créée pour ce client."
          : "Paiement en espèces enregistré."
      );

    } catch (error) {
      Alert.alert("Erreur", error.message || "Erreur lors de l'enregistrement");
    } finally {
      setChargement(false);
    }
  };

  // ── Rendu d'un produit (photo en fond + overlay lisible) ───
  const renderProduit = ({ item }) => {
    const ligneExistante  = panier.find((l) => l.produit.id === item.id);
    const qteSelectionnee = ligneExistante?.quantite || 0;

    const couleurStock =
      item.quantiteStock <= 0 ? COLORS.SCORE_ROUGE :
      item.quantiteStock <= item.stockMinimum ? COLORS.SCORE_ORANGE :
      COLORS.SCORE_VERT;

    const contenuCarte = (
      <>
        {/* Voile sombre pour lisibilité du texte sur la photo */}
        <View style={styles.produitOverlay} />

        {/* Badge quantité sélectionnée */}
        {qteSelectionnee > 0 && (
          <View style={styles.badgeQte}>
            <Text style={styles.badgeQteTexte}>x{qteSelectionnee}</Text>
          </View>
        )}

        {/* Stock haut droite, avec fond pour lisibilité */}
        <View style={styles.stockBadge}>
          <Text style={[styles.produitStock, { color: couleurStock }]}>
            {item.quantiteStock}
          </Text>
        </View>

        {/* Bandeau bas : nom + prix sur fond semi-opaque */}
        <View style={styles.produitInfoBandeau}>
          <Text style={styles.produitNom} numberOfLines={2}>
            {item.nom}
          </Text>
          <Text style={styles.produitPrix}>
            {formatFCFA(item.prixVente)}
          </Text>
        </View>
      </>
    );

    return (
      <TouchableOpacity
        style={[
          styles.produitCard,
          {
            borderWidth: qteSelectionnee > 0 ? 2 : 0,
            borderColor: COLORS.ORANGE,
            opacity: item.quantiteStock <= 0 ? 0.5 : 1,
          },
        ]}
        onPress={() => handleTapProduit(item)}
        activeOpacity={0.85}
        disabled={item.quantiteStock <= 0}
      >
        {item.photoUri ? (
          <ImageBackground
            source={{ uri: item.photoUri }}
            style={styles.produitImageFond}
            imageStyle={{ borderRadius: 14 }}
          >
            {contenuCarte}
          </ImageBackground>
        ) : (
          <View style={[styles.produitImageFond, { backgroundColor: cardCouleur }]}>
            <View style={styles.produitIconeFallback}>
              <Text style={{ fontSize: 30 }}>🛒</Text>
            </View>
            {contenuCarte}
          </View>
        )}
      </TouchableOpacity>
    );
  };

  // ── Rendu d'une transaction dans l'historique ──────────────
  const renderHistorique = ({ item }) => {
    const estCredit = item.typePaiement === PAIEMENT.CREDIT;

    return (
      <TouchableOpacity
        style={[styles.historiqueCard, { backgroundColor: cardCouleur }]}
        onPress={() => {
          setVenteDetail(item);
          setModalDetailVente(true);
        }}
        activeOpacity={0.85}
      >
        <View style={styles.historiqueHeader}>
          <View style={[
            styles.historiqueBadge,
            { backgroundColor: estCredit ? "#3DEF4444" : "#3D22C55E" }
          ]}>
            <Text style={{
              color: estCredit ? COLORS.SCORE_ROUGE : COLORS.SCORE_VERT,
              fontSize: 11, fontWeight: "700",
            }}>
              {estCredit ? "CRÉDIT" : "ESPÈCES"}
            </Text>
          </View>
          <Text style={styles.historiqueMontant}>
            {formatFCFA(item.montantTotal)}
          </Text>
        </View>

        <Text style={[styles.historiqueArticles, { color: textCouleur }]} numberOfLines={1}>
          {item.panier.length} article(s) : {item.panier.map(l => l.produit.nom).join(", ")}
        </Text>

        <View style={styles.historiqueFooter}>
          <Text style={[styles.historiqueInfo, { color: textSecondaire }]}>
            👤 {item.nomVendeur || "—"}
            {item.nomClient ? `  •  🧑 ${item.nomClient}` : ""}
          </Text>
        </View>
        <Text style={[styles.historiqueDate, { color: textSecondaire }]}>
          {formatDateHeure(item.createdAt)}
        </Text>
      </TouchableOpacity>
    );
  };

  // ── Rendu principal ────────────────────────────────────────
  return (
    <SafeAreaView style={[styles.container, { backgroundColor: bgCouleur }]}>
      <StatusBar
        barStyle={darkMode ? "light-content" : "dark-content"}
        backgroundColor={bgCouleur}
      />

      {/* ── Header ──────────────────────────────────────────── */}
      <View style={styles.header}>
        <View>
          <Text style={[styles.headerDate, { color: textCouleur }]}>
            {new Date().toLocaleDateString("fr-FR", { day: "numeric", month: "short" })}
          </Text>
          <Text style={styles.headerTotal}>{formatFCFA(totalJour, true)}</Text>
        </View>
      </View>

      {/* ── Toggle Produits / Historique ──────────────────────── */}
      <View style={styles.toggleRow}>
        <TouchableOpacity
          style={[
            styles.toggleBtn,
            { backgroundColor: onglet === "produits" ? COLORS.ORANGE : inputBg }
          ]}
          onPress={() => setOnglet("produits")}
        >
          <Text style={{
            color: onglet === "produits" ? COLORS.WHITE : textSecondaire,
            fontWeight: onglet === "produits" ? "700" : "400",
            fontSize: 13,
          }}>
            🛒 Produits
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            styles.toggleBtn,
            { backgroundColor: onglet === "historique" ? COLORS.ORANGE : inputBg }
          ]}
          onPress={() => setOnglet("historique")}
        >
          <Text style={{
            color: onglet === "historique" ? COLORS.WHITE : textSecondaire,
            fontWeight: onglet === "historique" ? "700" : "400",
            fontSize: 13,
          }}>
            📜 Historique
          </Text>
        </TouchableOpacity>
      </View>

      {/* ══════════════════════ ONGLET PRODUITS ══════════════════════ */}
      {onglet === "produits" && (
        <>
          {/* Barre de recherche */}
          <View style={[styles.searchBar, { backgroundColor: inputBg }]}>
            <Text style={styles.searchIcone}>🔍</Text>
            <TextInput
              style={[styles.searchInput, { color: textCouleur }]}
              placeholder="Rechercher un produit..."
              placeholderTextColor={textSecondaire}
              value={recherche}
              onChangeText={setRecherche}
            />
            {recherche.length > 0 && (
              <TouchableOpacity onPress={() => setRecherche("")}>
                <Text style={{ color: textSecondaire }}>✕</Text>
              </TouchableOpacity>
            )}
          </View>

          {/* Grille produits */}
          <FlatList
            data={produits}
            renderItem={renderProduit}
            keyExtractor={(item) => item.id.toString()}
            numColumns={2}
            contentContainerStyle={styles.grille}
            refreshControl={
              <RefreshControl refreshing={chargement} onRefresh={chargerProduits} colors={[COLORS.ORANGE]} />
            }
            ListEmptyComponent={
              <View style={styles.vide}>
                <Text style={styles.videIcone}>🛒</Text>
                <Text style={[styles.videTexte, { color: textCouleur }]}>
                  {recherche ? "Aucun produit trouvé" : "Aucun produit dans ce groupe"}
                </Text>
                <Text style={[styles.videSubTexte, { color: textSecondaire }]}>
                  Ajoutez des produits depuis l'onglet Stock
                </Text>
              </View>
            }
          />

          {/* ── Panier multi-lignes ──────────────────────────── */}
          {panier.length > 0 && (
            <View style={[styles.panier, { backgroundColor: cardCouleur }]}>
              <ScrollView style={{ maxHeight: 180 }} showsVerticalScrollIndicator={false}>
                {panier.map((ligne) => (
                  <View key={ligne.produit.id} style={styles.panierLigne}>
                    <Text
                      style={[styles.panierLigneNom, { color: textCouleur }]}
                      numberOfLines={1}
                    >
                      {ligne.produit.nom}
                    </Text>

                    <View style={styles.panierControles}>
                      <TouchableOpacity
                        style={styles.btnQte}
                        onPress={() => decrementerLigne(ligne.produit.id)}
                      >
                        <Text style={styles.btnQteTexte}>−</Text>
                      </TouchableOpacity>

                      <Text style={[styles.panierQte, { color: textCouleur }]}>
                        {ligne.quantite}
                      </Text>

                      <TouchableOpacity
                        style={styles.btnQte}
                        onPress={() => incrementerLigne(ligne.produit)}
                      >
                        <Text style={styles.btnQteTexte}>+</Text>
                      </TouchableOpacity>
                    </View>

                    <Text style={styles.panierSousTotal}>
                      {formatFCFA(ligne.sousTotal)}
                    </Text>
                  </View>
                ))}
              </ScrollView>

              <View style={styles.panierFooter}>
                <View>
                  <Text style={[styles.panierTotalLabel, { color: textSecondaire }]}>
                    {getNbArticles()} article(s)
                  </Text>
                  <Text style={styles.panierTotalMontant}>
                    {formatFCFA(getTotalPanier())}
                  </Text>
                </View>
                <TouchableOpacity onPress={() => {
                  Alert.alert("Vider le panier", "Supprimer tous les articles ?", [
                    { text: "Annuler", style: "cancel" },
                    { text: "Vider", onPress: viderPanier },
                  ]);
                }}>
                  <Text style={{ color: COLORS.ERROR, fontSize: 13 }}>Vider</Text>
                </TouchableOpacity>
              </View>

              <View style={styles.panierBoutons}>
                <TouchableOpacity
                  style={styles.btnEspeces}
                  onPress={() => ouvrirModalPaiement(PAIEMENT.ESPECES)}
                >
                  <Text style={styles.btnEspecesTexte}>💵 Espèces</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={styles.btnCredit}
                  onPress={() => ouvrirModalPaiement(PAIEMENT.CREDIT)}
                >
                  <Text style={styles.btnCreditTexte}>💳 Crédit</Text>
                </TouchableOpacity>
              </View>
            </View>
          )}
        </>
      )}

      {/* ══════════════════════ ONGLET HISTORIQUE ══════════════════════ */}
      {onglet === "historique" && (
        <>
          {/* Filtres tri */}
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            style={styles.filtresRow}
            contentContainerStyle={{ paddingHorizontal: 16, gap: 8 }}
          >
            {[
              { id: "tout",        label: "Tout" },
              { id: "aujourd_hui", label: "Aujourd'hui" },
              { id: "hier",        label: "Hier" },
              { id: "especes",     label: "Espèces" },
              { id: "credit",      label: "Crédit" },
            ].map((f) => (
              <TouchableOpacity
                key={f.id}
                style={[
                  styles.filtreChip,
                  { backgroundColor: filtreHisto === f.id ? COLORS.ORANGE : inputBg }
                ]}
                onPress={() => setFiltreHisto(f.id)}
              >
                <Text style={{
                  color: filtreHisto === f.id ? COLORS.WHITE : textSecondaire,
                  fontSize: 12,
                  fontWeight: filtreHisto === f.id ? "700" : "400",
                }}>
                  {f.label}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>

          <FlatList
            data={historique}
            renderItem={renderHistorique}
            keyExtractor={(item) => item.id.toString()}
            contentContainerStyle={styles.listeHistorique}
            refreshControl={
              <RefreshControl refreshing={chargement} onRefresh={chargerHistorique} colors={[COLORS.ORANGE]} />
            }
            ListEmptyComponent={
              <View style={styles.vide}>
                <Text style={styles.videIcone}>📜</Text>
                <Text style={[styles.videTexte, { color: textCouleur }]}>
                  Aucune vente enregistrée
                </Text>
              </View>
            }
          />
        </>
      )}

      {/* ── Modal Paiement (espèces / crédit) ─────────────────── */}
      <Modal
        visible={modalPaiement}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalPaiement(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={[styles.modalContainer, { backgroundColor: cardCouleur }]}>
            <Text style={[styles.modalTitre, { color: textCouleur }]}>
              {typePaiementEnCours === PAIEMENT.CREDIT ? "Vente à crédit" : "Vente en espèces"}
            </Text>

            <ScrollView showsVerticalScrollIndicator={false}>

              {/* Tabs client existant / nouveau */}
              <View style={styles.modalTabs}>
                <TouchableOpacity
                  style={[styles.modalTab, !modeNouveauCli && styles.modalTabActif]}
                  onPress={() => { setModeNouveauCli(false); setClientChoisi(null); }}
                >
                  <Text style={[styles.modalTabTexte, !modeNouveauCli && styles.modalTabTexteActif]}>
                    Client existant
                  </Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={[styles.modalTab, modeNouveauCli && styles.modalTabActif]}
                  onPress={() => { setModeNouveauCli(true); setClientChoisi(null); }}
                >
                  <Text style={[styles.modalTabTexte, modeNouveauCli && styles.modalTabTexteActif]}>
                    Nouveau client
                  </Text>
                </TouchableOpacity>
              </View>

              {typePaiementEnCours === PAIEMENT.ESPECES && (
                <Text style={[styles.infoOptionnel, { color: textSecondaire }]}>
                  ℹ Renseigner un client est optionnel pour une vente en espèces.
                </Text>
              )}

              {/* Recherche / liste clients existants */}
              {!modeNouveauCli && (
                <View>
                  <Input
                    placeholder="Rechercher par nom ou téléphone..."
                    valeur={rechercheClient}
                    onChange={handleRechercheClient}
                    icone="🔍"
                    dark={darkMode}
                  />
                  {clientsTrouves.length === 0 ? (
                    <Text style={[styles.aucunClient, { color: textSecondaire }]}>
                      Aucun client trouvé.{"\n"}Créez un nouveau client.
                    </Text>
                  ) : (
                    clientsTrouves.map((client) => (
                      <TouchableOpacity
                        key={client.id}
                        style={[
                          styles.clientItem,
                          { backgroundColor: inputBg },
                          clientChoisi?.id === client.id && styles.clientItemSelectionne,
                        ]}
                        onPress={() => setClientChoisi(client)}
                      >
                        <Text style={[styles.clientNom, { color: textCouleur }]}>
                          {client.nomClient}
                        </Text>
                        <Text style={[styles.clientTel, { color: textSecondaire }]}>
                          {client.numeroClient}
                        </Text>
                        <View style={[
                          styles.clientScore,
                          { borderColor: client.score >= 60 ? COLORS.SCORE_VERT : COLORS.SCORE_ROUGE }
                        ]}>
                          <Text style={{
                            color: client.score >= 60 ? COLORS.SCORE_VERT : COLORS.SCORE_ROUGE,
                            fontSize: 11, fontWeight: "700",
                          }}>
                            {client.score}
                          </Text>
                        </View>
                      </TouchableOpacity>
                    ))
                  )}
                </View>
              )}

              {/* Mode nouveau client */}
              {modeNouveauCli && (
                <View>
                  <Input
                    label="Nom du client"
                    placeholder="Ex: Moussa Diallo"
                    valeur={nomNouveauCli}
                    onChange={setNomNouveauCli}
                    dark={darkMode}
                  />
                  <Input
                    label="Téléphone"
                    placeholder="+237 6XX XXX XXX"
                    valeur={telNouveauCli}
                    onChange={setTelNouveauCli}
                    typeClavier="phone-pad"
                    dark={darkMode}
                    autoCapitalize="none"
                  />
                </View>
              )}

              {/* Date de remboursement (crédit uniquement) */}
              {typePaiementEnCours === PAIEMENT.CREDIT && (
                <Input
                  label="Date de remboursement *"
                  placeholder="JJ/MM/AAAA"
                  valeur={dateRemb}
                  onChange={setDateRemb}
                  typeClavier="numeric"
                  dark={darkMode}
                />
              )}

              {/* Total */}
              <View style={styles.modalTotal}>
                <Text style={[styles.modalTotalLabel, { color: textCouleur }]}>
                  Montant total :
                </Text>
                <Text style={styles.modalTotalMontant}>
                  {formatFCFA(getTotalPanier())}
                </Text>
              </View>

              <Button
                titre="Confirmer la vente"
                onPress={confirmerPaiement}
                style={{ marginBottom: 12 }}
              />
              <Button
                titre="Annuler"
                variante="secondary"
                onPress={() => setModalPaiement(false)}
              />
            </ScrollView>
          </View>
        </View>
      </Modal>

      {/* ── Modal Détail Vente (historique) ───────────────────── */}
      <Modal
        visible={modalDetailVente}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalDetailVente(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={[styles.modalContainer, { backgroundColor: cardCouleur }]}>
            {venteDetail && (
              <>
                <Text style={[styles.modalTitre, { color: textCouleur }]}>
                  Détail de la vente
                </Text>

                <Text style={[styles.detailInfo, { color: textSecondaire }]}>
                  📅 {formatDateHeure(venteDetail.createdAt)}
                </Text>
                <Text style={[styles.detailInfo, { color: textSecondaire }]}>
                  👤 Vendeur : {venteDetail.nomVendeur || "—"}
                </Text>
                {venteDetail.nomClient && (
                  <Text style={[styles.detailInfo, { color: textSecondaire }]}>
                    🧑 Client : {venteDetail.nomClient}
                  </Text>
                )}
                <Text style={[styles.detailInfo, { color: textSecondaire }]}>
                  💳 Paiement : {venteDetail.typePaiement === PAIEMENT.CREDIT ? "Crédit" : "Espèces"}
                </Text>

                <ScrollView style={{ maxHeight: 220, marginTop: 10 }}>
                  {venteDetail.panier.map((ligne, idx) => (
                    <View key={idx} style={styles.detailLigne}>
                      <Text style={[styles.detailLigneNom, { color: textCouleur }]}>
                        {ligne.produit.nom} x{ligne.quantite}
                      </Text>
                      <Text style={styles.detailLigneMontant}>
                        {formatFCFA(ligne.sousTotal)}
                      </Text>
                    </View>
                  ))}
                </ScrollView>

                <View style={styles.modalTotal}>
                  <Text style={[styles.modalTotalLabel, { color: textCouleur }]}>
                    Total :
                  </Text>
                  <Text style={styles.modalTotalMontant}>
                    {formatFCFA(venteDetail.montantTotal)}
                  </Text>
                </View>

                <Button
                  titre="Fermer"
                  variante="secondary"
                  onPress={() => setModalDetailVente(false)}
                />
              </>
            )}
          </View>
        </View>
      </Modal>

    </SafeAreaView>
  );
};

// ── Styles ─────────────────────────────────────────────────────
const styles = StyleSheet.create({
  container: { flex: 1 },

  header: {
    flexDirection:     "row",
    justifyContent:    "space-between",
    alignItems:        "center",
    paddingHorizontal: 16,
    paddingVertical:   12,
  },
  headerDate:  { fontSize: 16, fontWeight: "700" },
  headerTotal: { fontSize: 15, fontWeight: "700", color: COLORS.ORANGE },

  toggleRow: {
    flexDirection:     "row",
    paddingHorizontal: 16,
    gap:               8,
    marginBottom:      10,
  },
  toggleBtn: {
    flex:           1,
    height:         40,
    borderRadius:   10,
    alignItems:     "center",
    justifyContent: "center",
  },

  searchBar: {
    flexDirection:     "row",
    alignItems:        "center",
    marginHorizontal:  16,
    marginBottom:      12,
    borderRadius:      12,
    paddingHorizontal: 14,
    height:            46,
  },
  searchIcone: { fontSize: 16, marginRight: 8 },
  searchInput: { flex: 1, fontSize: 14, height: "100%" },

  grille: { paddingHorizontal: 12, paddingBottom: 220 },

  // ── Carte produit avec photo en fond ───────────────────────
  produitCard: {
    width:        CARD_WIDTH,
    height:       140,
    borderRadius: 14,
    margin:       6,
    overflow:     "hidden",
    shadowColor:  "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation:    2,
  },
  produitImageFond: {
    flex: 1,
    justifyContent: "flex-end",
  },
  produitIconeFallback: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  produitOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: "rgba(0,0,0,0.15)",
  },
  badgeQte: {
    position:          "absolute",
    top:               8,
    left:              8,
    backgroundColor:   COLORS.ORANGE,
    borderRadius:      12,
    paddingHorizontal: 8,
    paddingVertical:   2,
    zIndex:            2,
  },
  badgeQteTexte: { color: COLORS.WHITE, fontSize: 11, fontWeight: "700" },

  stockBadge: {
    position:          "absolute",
    top:               8,
    right:             8,
    backgroundColor:   "rgba(0,0,0,0.55)",
    borderRadius:      10,
    paddingHorizontal: 8,
    paddingVertical:   2,
    zIndex:            2,
  },
  produitStock: { fontSize: 13, fontWeight: "800" },

  // Bandeau bas semi-opaque pour lisibilité sur la photo
  produitInfoBandeau: {
    backgroundColor:   "rgba(0,0,0,0.55)",
    paddingHorizontal: 10,
    paddingVertical:   8,
  },
  produitNom: {
    fontSize:   13,
    fontWeight: "700",
    color:      COLORS.WHITE,
  },
  produitPrix: {
    fontSize:   13,
    fontWeight: "800",
    color:      COLORS.ORANGE_LIGHT,
    marginTop:  2,
  },

  vide: { alignItems: "center", paddingTop: 80 },
  videIcone: { fontSize: 48, marginBottom: 16 },
  videTexte: { fontSize: 16, fontWeight: "600", marginBottom: 8 },
  videSubTexte: { fontSize: 13 },

  // ── Panier ────────────────────────────────────────────────
  panier: {
    position:             "absolute",
    bottom:                0,
    left:                  0,
    right:                 0,
    padding:               16,
    borderTopLeftRadius:   20,
    borderTopRightRadius:  20,
    shadowColor:           "#000",
    shadowOffset:          { width: 0, height: -4 },
    shadowOpacity:         0.12,
    shadowRadius:          12,
    elevation:             8,
  },
  panierLigne: {
    flexDirection:     "row",
    alignItems:        "center",
    paddingVertical:   8,
    borderBottomWidth: 1,
    borderBottomColor: "rgba(150,150,150,0.2)",
  },
  panierLigneNom: { flex: 1, fontSize: 13 },
  panierControles: {
    flexDirection:     "row",
    alignItems:        "center",
    gap:               8,
    marginHorizontal:  10,
  },
  btnQte: {
    width: 26, height: 26, borderRadius: 13,
    backgroundColor: COLORS.ORANGE,
    alignItems: "center", justifyContent: "center",
  },
  btnQteTexte: { color: COLORS.WHITE, fontSize: 16, fontWeight: "700", lineHeight: 20 },
  panierQte: { fontSize: 14, fontWeight: "700", minWidth: 22, textAlign: "center" },
  panierSousTotal: { fontSize: 12, color: COLORS.ORANGE, minWidth: 78, textAlign: "right" },

  panierFooter: {
    flexDirection:   "row",
    justifyContent:  "space-between",
    alignItems:      "center",
    paddingVertical: 10,
  },
  panierTotalLabel:  { fontSize: 11 },
  panierTotalMontant:{ fontSize: 17, fontWeight: "800", color: COLORS.ORANGE },

  panierBoutons: { flexDirection: "row", gap: 12 },
  btnEspeces: {
    flex: 1, height: 50, backgroundColor: COLORS.BG_DARK,
    borderRadius: 14, alignItems: "center", justifyContent: "center",
  },
  btnEspecesTexte: { color: COLORS.WHITE, fontSize: 14, fontWeight: "700" },
  btnCredit: {
    flex: 1, height: 50, backgroundColor: "transparent",
    borderRadius: 14, alignItems: "center", justifyContent: "center",
    borderWidth: 1, borderColor: COLORS.ORANGE,
  },
  btnCreditTexte: { color: COLORS.ORANGE, fontSize: 14, fontWeight: "700" },

  // ── Historique ────────────────────────────────────────────
  filtresRow: { marginBottom: 10, maxHeight: 40 },
  filtreChip: {
    paddingHorizontal: 14, paddingVertical: 8,
    borderRadius: 20, justifyContent: "center",
  },
  listeHistorique: { paddingHorizontal: 16, paddingBottom: 40 },
  historiqueCard: {
    borderRadius: 14, padding: 14, marginBottom: 10,
  },
  historiqueHeader: {
    flexDirection: "row", justifyContent: "space-between",
    alignItems: "center", marginBottom: 6,
  },
  historiqueBadge: { paddingHorizontal: 10, paddingVertical: 3, borderRadius: 20 },
  historiqueMontant: { fontSize: 16, fontWeight: "800", color: COLORS.ORANGE },
  historiqueArticles: { fontSize: 13, marginBottom: 6 },
  historiqueFooter: { marginBottom: 2 },
  historiqueInfo: { fontSize: 12 },
  historiqueDate: { fontSize: 11 },

  // ── Modal commun ──────────────────────────────────────────
  modalOverlay: {
    flex: 1, backgroundColor: "rgba(0,0,0,0.5)", justifyContent: "flex-end",
  },
  modalContainer: {
    borderTopLeftRadius: 24, borderTopRightRadius: 24,
    padding: 24, maxHeight: "85%",
  },
  modalTitre: { fontSize: 18, fontWeight: "800", marginBottom: 14 },

  modalTabs: {
    flexDirection: "row", backgroundColor: "rgba(150,150,150,0.15)",
    borderRadius: 12, padding: 4, marginBottom: 12,
  },
  modalTab: { flex: 1, height: 40, borderRadius: 10, alignItems: "center", justifyContent: "center" },
  modalTabActif: { backgroundColor: COLORS.ORANGE },
  modalTabTexte: { fontSize: 13, color: COLORS.TEXT_SECONDARY_LIGHT },
  modalTabTexteActif: { color: COLORS.WHITE, fontWeight: "700" },

  infoOptionnel: { fontSize: 12, marginBottom: 10, fontStyle: "italic" },

  clientItem: {
    flexDirection: "row", alignItems: "center",
    padding: 12, borderRadius: 12, marginBottom: 8,
  },
  clientItemSelectionne: { borderWidth: 2, borderColor: COLORS.ORANGE },
  clientNom: { flex: 1, fontSize: 14, fontWeight: "600" },
  clientTel: { fontSize: 12, marginRight: 8 },
  clientScore: {
    width: 34, height: 34, borderRadius: 17,
    borderWidth: 2, alignItems: "center", justifyContent: "center",
  },
  aucunClient: { textAlign: "center", fontSize: 14, paddingVertical: 16 },

  modalTotal: {
    flexDirection: "row", justifyContent: "space-between", alignItems: "center",
    paddingVertical: 14, borderTopWidth: 1, borderTopColor: "rgba(150,150,150,0.2)",
    marginTop: 10, marginBottom: 14,
  },
  modalTotalLabel: { fontSize: 15, fontWeight: "600" },
  modalTotalMontant: { fontSize: 18, fontWeight: "800", color: COLORS.ORANGE },

  detailInfo: { fontSize: 13, marginBottom: 4 },
  detailLigne: {
    flexDirection: "row", justifyContent: "space-between",
    paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: "rgba(150,150,150,0.15)",
  },
  detailLigneNom: { fontSize: 13, flex: 1 },
  detailLigneMontant: { fontSize: 13, color: COLORS.ORANGE, fontWeight: "700" },
});

export default VentesScreen;

