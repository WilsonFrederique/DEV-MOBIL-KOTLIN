import pool from "../config/db.config.js";

async function create(req, res) {
  try {
    const { design, loyer } = req.body;

    if (!design || !loyer) {
      return res.status(400).json({ error: "Designation ou Loyer invalide" });
    }

    const [result] = await pool.query(
      "INSERT INTO appartements (design, loyer) VALUES (?, ?)",
      [design, loyer]
    );

    return res.status(201).json({
      message: "Appartement ajouté avec succès",
      numApp: result.insertId, // Retourne l'ID généré
    });
  } catch (error) {
    return res.status(500).json({ error: "Erreur serveur: " + error.message });
  }
}

async function updateOne(req, res) {
  try {
    const { design, loyer } = req.body;
    const { numApp } = req.params;

    if (!design || !loyer) {
      return res.status(400).json({ error: "Designation ou Loyer invalide" });
    }

    const [result] = await pool.query(
      "UPDATE appartements SET design = ?, loyer = ? WHERE numApp = ?",
      [design, loyer, numApp]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: "Appartement non trouvé" });
    }

    return res.status(200).json({
      message: "Appartement modifié avec succès",
      data: { numApp, design, loyer },
    });
  } catch (error) {
    return res.status(500).json({ error: "Erreur serveur: " + error.message });
  }
}

async function deleteOne(req, res) {
  try {
    const { numApp } = req.params;

    const [result] = await pool.query(
      "DELETE FROM appartements WHERE numApp = ?",
      [numApp]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: "Appartement non trouvé" });
    }

    return res.status(200).json({
      message: "Suppression effectuée avec succès",
    });
  } catch (error) {
    return res.status(500).json({ error: "Erreur serveur: " + error.message });
  }
}

async function getAll(req, res) {
  try {
    const [result] = await pool.query("SELECT * FROM appartements");
    return res.status(200).json(result);
  } catch (error) {
    return res.status(500).json({ error: "Erreur serveur: " + error.message });
  }
}

async function getOne(req, res) {
  try {
    const { numApp } = req.params;
    const [result] = await pool.query(
      "SELECT * FROM appartements WHERE numApp = ?",
      [numApp]
    );

    if (result.length === 0) {
      return res.status(404).json({ error: "Appartement non trouvé" });
    }

    return res.status(200).json(result[0]);
  } catch (error) {
    return res.status(500).json({ error: "Erreur serveur: " + error.message });
  }
}

export default {
  create,
  updateOne,
  deleteOne,
  getAll,
  getOne,
};
