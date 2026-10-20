package ua.govstack.digitalwallet.app.ui_base.components.molecule.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnNumAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnNumAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.IconBiometricAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.IconRemoveNumAtm
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.theme.Honeydew

@Composable
fun NumButtonTileMlc(
    modifier: Modifier = Modifier,
    hasBiometric: Boolean = false,
    color: Color = Honeydew,
    onUIAction: (UIAction) -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            Modifier
                .height(75.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "1", number = 1),
                color = color,
                onUIAction = onUIAction
            )
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp)
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "2", number = 2),
                color = color,
                onUIAction = onUIAction
            )
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "3", number = 3),
                color = color,
                onUIAction = onUIAction
            )
        }

        Spacer(
            modifier = Modifier
                .size(16.dp)
        )

        Row(
            Modifier
                .height(75.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "4", number = 4),
                color = color,
                onUIAction = onUIAction
            )
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp)
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "5", number = 5),
                color = color,
                onUIAction = onUIAction
            )
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "6", number = 6),
                color = color,
                onUIAction = onUIAction
            )
        }

        Spacer(
            modifier = Modifier
                .size(16.dp)
        )

        Row(
            Modifier
                .height(75.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "7", number = 7),
                color = color,
                onUIAction = onUIAction
            )
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp)
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "8", number = 8),
                color = color,
                onUIAction = onUIAction
            )
            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "9", number = 9),
                color = color,
                onUIAction = onUIAction
            )
        }

        Spacer(
            modifier = Modifier
                .size(16.dp)
        )

        Row(
            Modifier
                .height(75.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (hasBiometric) {
                IconBiometricAtm(
                    modifier = Modifier
                        .size(75.dp)
                        .semantics {
                            role = Role.Button
                        },
                    onUIAction = onUIAction
                )
            } else {
                // For proper line arrangement
                Spacer(
                    modifier = Modifier
                        .size(75.dp)
                )
            }

            BtnNumAtm(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp)
                    .semantics {
                        role = Role.Button
                    },
                data = BtnNumAtmData(id = "0", number = 0),
                color = color,
                onUIAction = onUIAction
            )

            IconRemoveNumAtm(
                modifier = Modifier
                    .size(75.dp)
                    .semantics {
                        role = Role.Button
                    },
                onUIAction = onUIAction
            )
        }
    }
}


@Composable
@Preview
fun NumButtonTileMoleculePreview() {
    NumButtonTileMlc(
        hasBiometric = false
    ) {
        /* no-op */
    }
}

@Composable
@Preview
fun NumButtonTileMoleculeWithBiometricPreview() {
    NumButtonTileMlc(
        hasBiometric = true
    ) {
        /* no-op */
    }
}