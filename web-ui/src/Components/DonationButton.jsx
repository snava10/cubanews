import React, { useEffect, useRef } from "react";
import { Alert } from "@mui/material";
import IconButton from "@material-ui/core/IconButton";
import CloseIcon from "@material-ui/icons/Close";
import { Collapse } from "@material-ui/core";

export function DonationButton() {
  const buttonRef = useRef(null);
  const buttonId = "donate-button";
  const [open, setOpen] = React.useState(true);

  useEffect(() => {
    const button = window.PayPal.Donation.Button({
      env: "production",
      hosted_button_id: "FZH2CBW5RCZC2",
      image: {
        src: "https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif",
        alt: "Donate with PayPal button",
        title: "PayPal - The safer, easier way to pay online!",
      },
    });
    button.render(`#${buttonRef.current.id}`); // you can change the code and run it when DOM is ready
  }, []);
  return (
    <div>
      <Collapse in={open}>
        <Alert
          action={
            <IconButton
              aria-label="close"
              color="inherit"
              size="small"
              onClick={() => {
                setOpen(false);
              }}
            >
              <CloseIcon fontSize="inherit" />
            </IconButton>
          }
          severity="warning"
        >
          Este sitio ha sido creado en su totalidad por voluntarios en su tiempo
          libre, y se mantiene con fondos personales. Si desea apoyarnos en
          nuestro empeño por divulgar la verdad sobre la situación de Cuba, por
          favor utilice el botón para donar. Todo lo recaudado se destinará a
          mantener y expandir Cubanews. Muchas gracias.
          <div ref={buttonRef} id={buttonId} />
        </Alert>
      </Collapse>
    </div>
  );

  // return (
  //     <div id="donate-button-container">
  //         <div id="donate-button"></div>
  //         <script src="https://www.paypalobjects.com/donate/sdk/donate-sdk.js" charset="UTF-8"></script>
  //         <script>
  //         PayPal.Donation.Button({env:'production', hosted_button_id:'FZH2CBW5RCZC2', image: {
  //             src:'https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif', alt:'Donate with PayPal button',
  //             title:'PayPal - The safer, easier way to pay online!',
  //         }}).render('#donate-button');
  //         </script>
  //     </div>
  // )
}
