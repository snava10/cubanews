import {
  Alert,
  Typography,
  Button,
  Checkbox,
  Input,
  FormControl,
  Stack,
} from "@mui/joy";
import React from "react";
import { ResolveNewsletterSubscriptionData } from "../interfaces";

export default function NewsLetterSubscriptionComponent({ onResolve }: any) {
  const [dontShowAgain, setDontShowAgain] = React.useState(false);
  const [data, setData] = React.useState<{
    email: string;
    status: "initial" | "failure" | "sent";
  }>({
    email: "",
    status: "initial",
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setData((current) => ({ ...current, status: "initial" }));
    try {
      onResolve({
        operation: "subscribe",
        email: data.email,
        dontShowAgain,
      } as ResolveNewsletterSubscriptionData);
    } catch (error) {
      setData((current) => ({ ...current, status: "failure" }));
    }
  };

  return (
    <Alert color="warning" variant="soft">
      <Stack spacing={2}>
        <Typography level="h3" sx={{ mb: 2, ml: 1 }}>
          Recibe CubaNews via email, subscribete a nuestro resumen diario.
        </Typography>
        <form onSubmit={handleSubmit}>
          <FormControl>
            <Input
              sx={{ "--Input-decoratorChildHeight": "45px" }}
              placeholder="email@email.com"
              type="email"
              required
              value={data.email}
              onChange={(event) =>
                setData({
                  email: event.target.value,
                  status: "initial",
                })
              }
              error={data.status === "failure"}
              endDecorator={
                <Button
                  variant="solid"
                  color="primary"
                  type="submit"
                  sx={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }}
                >
                  Subscribe
                </Button>
              }
            />
          </FormControl>
        </form>
        <Stack direction="row" spacing={2}>
          <Checkbox
            label="No volver a mostrar este mensaje"
            checked={dontShowAgain}
            onChange={(event) => setDontShowAgain(event.target.checked)}
          />
          <Button
            variant="solid"
            color="danger"
            type="submit"
            onClick={() =>
              onResolve({
                operation: "close",
                email: data.email,
                dontShowAgain,
              })
            }
          >
            Cerrar
          </Button>
        </Stack>
      </Stack>
    </Alert>
  );
}
