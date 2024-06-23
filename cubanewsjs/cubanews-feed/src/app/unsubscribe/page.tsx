"use client";
import {
  Divider,
  Chip,
  Button,
  FormControl,
  Container,
  Input,
  Alert,
  Stack,
  Typography,
} from "@mui/joy";
import CopyrightIcon from "@mui/icons-material/Copyright";
import { useState } from "react";

export default function Unsubscribe() {
  const [confirmation, setConfirmation] = useState<boolean>(false);
  const [email, setEmail] = useState<string>("");

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    fetch("/api/subscriptions", {
      method: "POST",
      body: JSON.stringify({
        email: email,
        operation: "unsubscribe",
      }),
    })
      .then((res) => {
        if (res.status === 200) {
          setConfirmation(true);
        }
      })
      .catch((error) => {
        console.log("Unsubscribe failed ", error);
      });
  };

  return (
    <Container style={{ paddingTop: 30 }}>
      <Stack spacing={2}>
        {confirmation ? (
          <Alert variant={"soft"} color="success">
            <Typography level="h3">
              Su suscripción ha sido cancelada.
            </Typography>
          </Alert>
        ) : null}
        <form onSubmit={handleSubmit}>
          <FormControl>
            <Input
              sx={{ "--Input-decoratorChildHeight": "45px" }}
              placeholder="email@email.com"
              type="email"
              required
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              endDecorator={
                <Button
                  variant="solid"
                  color="primary"
                  type="submit"
                  sx={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }}
                >
                  Darse de baja
                </Button>
              }
            />
          </FormControl>
        </form>
      </Stack>
      <Divider sx={{ m: 4 }}>
        <Chip
          variant="outlined"
          startDecorator={<CopyrightIcon />}
          sx={{ p: 1 }}
        >
          Cuba News
        </Chip>
      </Divider>
    </Container>
  );
}
