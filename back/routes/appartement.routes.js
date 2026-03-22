import express from "express";

import appartementController from "../controllers/appartement.controller.js";

const router = express.Router();

router.get("/appartement/", appartementController.getAll);
router.get("/appartement/:numApp", appartementController.getOne);
router.post("/appartement/", appartementController.create);
router.put("/appartement/:numApp", appartementController.updateOne);
router.delete("/appartement/:numApp", appartementController.deleteOne);

export default router;
