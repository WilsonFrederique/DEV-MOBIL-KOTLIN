import express from "express";
import dotenv from "dotenv";
import cors from "cors"
import router from "./routes/appartement.routes.js";

dotenv.config({ path: ".env" });

const app = express();
const port = process.env.PORT || 3000;

app.use(
  cors({
    origin: "*",
    methods: ["GET", "POST", "PATCH", "DELETE", "PUT"],
  }),
);
app.use(express.json());

// MIDDLEWARE DE DÉBOGAGE - Ajoutez ceci
app.use((req, res, next) => {
  console.log("=== DÉBOGAGE ===");
  console.log("Method:", req.method);
  console.log("URL:", req.url);
  console.log("Headers:", req.headers);
  console.log("Body:", req.body);
  console.log("================");
  next();
});

app.use("/api", router);

app.listen(port, () => {
  console.log(`App listening on port ${port}`);
  console.log(process.env.PORT);
});