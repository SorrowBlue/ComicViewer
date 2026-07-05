@file:Suppress("detekt.all")

package com.sorrowblue.comicviewer.framework.designsystem.icon.symbols

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Suppress("UnusedReceiverParameter")
val ComicIcons.LocalAccessNetwork: ImageVector
    get() {
        val current = _localAccessNetwork
        if (current != null) return current

        return ImageVector.Builder(
            name = "LocalAccessNetwork",
            defaultWidth = 256.0.dp,
            defaultHeight = 256.0.dp,
            viewportWidth = 256.0f,
            viewportHeight = 256.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF8F9FF)),
            ) {
                moveTo(x = 0.0f, y = 48.0f)
                arcToRelative(
                    a = 48.0f,
                    b = 48.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 48.0f,
                    dy1 = -48.0f,
                )
                horizontalLineToRelative(dx = 160.0f)
                arcToRelative(
                    a = 48.0f,
                    b = 48.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 48.0f,
                    dy1 = 48.0f,
                )
                verticalLineToRelative(dy = 160.0f)
                arcToRelative(
                    a = 48.0f,
                    b = 48.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -48.0f,
                    dy1 = 48.0f,
                )
                horizontalLineToRelative(dx = -160.0f)
                arcToRelative(
                    a = 48.0f,
                    b = 48.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -48.0f,
                    dy1 = -48.0f,
                )
                close()
            }
            group {
                path {
                    moveTo(x = 128.0f, y = 128.0f)
                    moveToRelative(dx = -79.5f, dy = 0.0f)
                    arcToRelative(
                        a = 79.5f,
                        b = 79.5f,
                        theta = 0.0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = 159.0f,
                        dy1 = 0.0f,
                    )
                    arcToRelative(
                        a = 79.5f,
                        b = 79.5f,
                        theta = 0.0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = -159.0f,
                        dy1 = 0.0f,
                    )
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF2563EB)),
                    fillAlpha = 0.2f,
                ) {
                    moveTo(x = 207.0f, y = 128.0f)
                    lineTo(x = 208.0f, y = 128.0f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 207.90002083159732f,
                        y1 = 131.99833354165426f,
                    )
                    lineTo(x = 206.90127057120233f, y = 131.9483543723836f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 207.0f,
                        y1 = 128.0f,
                    )
                    close()
                    moveTo(x = 206.60532905696402f, y = 135.88683991509942f)
                    lineTo(x = 207.60033322224206f, y = 135.98667333174626f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 207.10168623488337f,
                        y1 = 139.95505059788795f,
                    )
                    lineTo(x = 206.11291515694734f, y = 139.80561246541433f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 206.60532905696402f,
                        y1 = 135.88683991509942f,
                    )
                    close()
                    moveTo(x = 205.4252596494581f, y = 143.69487713280984f)
                    lineTo(x = 206.40532622729933f, y = 143.8935464636049f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 205.51299373685157f,
                        y1 = 147.79231674036183f,
                    )
                    lineTo(x = 204.54408131514094f, y = 147.5449127811073f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 205.4252596494581f,
                        y1 = 143.69487713280984f,
                    )
                    close()
                    moveTo(x = 203.47158264092286f, y = 151.34609632624583f)
                    lineTo(x = 204.42691913004847f, y = 151.64161653290716f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 203.1498170277903f,
                        y1 = 155.43182459643612f,
                    )
                    lineTo(x = 202.21044431494295f, y = 155.08892678898064f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 203.47158264092286f,
                        y1 = 151.34609632624583f,
                    )
                    close()
                    moveTo(x = 200.7638185262279f, y = 158.7640490423834f)
                    lineTo(x = 201.68487952023082f, y = 159.15346738469202f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 200.03576818821415f,
                        y1 = 162.79724272889842f,
                    )
                    lineTo(x = 199.13532108586148f, y = 162.36227719478717f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 200.7638185262279f,
                        y1 = 158.7640490423834f,
                    )
                    close()
                    moveTo(x = 197.32902238933946f, y = 165.87461754973202f)
                    lineTo(x = 198.20660495122982f, y = 166.35404308833623f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 196.20196176476048f,
                        y1 = 169.81497831445273f,
                    )
                    lineTo(x = 195.34943724270096f, y = 169.29229108552207f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 197.32902238933946f,
                        y1 = 165.87461754973202f,
                    )
                    close()
                    moveTo(x = 193.2015135778646f, y = 172.60675539820778f)
                    lineTo(x = 194.02684919277425f, y = 173.17139787160284f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 191.68670388392445f,
                        y1 = 176.41491245888318f,
                    )
                    lineTo(x = 190.89062008537542f, y = 175.80972605314713f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 193.2015135778646f,
                        y1 = 172.60675539820778f,
                    )
                    close()
                    moveTo(x = 188.42253279547458f, y = 178.8931972917776f)
                    lineTo(x = 189.18737498275908f, y = 179.5374149790153f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 186.53510950990568f,
                        y1 = 182.53110080186673f,
                    )
                    lineTo(x = 185.80342064103183f, y = 181.8494620418434f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 188.42253279547458f,
                        y1 = 178.8931972917776f,
                    )
                    close()
                    moveTo(x = 183.03983003842606f, y = 184.67113118106232f)
                    lineTo(x = 183.73653674777321f, y = 185.38848727196182f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 180.79865167079856f,
                        y1 = 188.10243241122342f,
                    )
                    lineTo(x = 180.13866852491358f, y = 187.35115200608314f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 183.03983003842606f,
                        y1 = 184.67113118106232f,
                    )
                    close()
                    moveTo(x = 177.10718749338247f, y = 189.8828258605712f)
                    lineTo(x = 177.72879746165313f, y = 190.66615277019866f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 174.53464715711067f,
                        y1 = 193.07324038314994f,
                    )
                    lineTo(x = 173.95296406764677f, y = 192.25982487836055f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 177.10718749338247f,
                        y1 = 189.8828258605712f,
                    )
                    close()
                    moveTo(x = 170.683882163583f, y = 194.47620779982384f)
                    lineTo(x = 171.22418446945116f, y = 195.3176787846317f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 167.80568383133814f,
                        y1 = 197.39385804752135f,
                    )
                    lineTo(x = 167.3081127834464f, y = 196.52643482192735f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 170.683882163583f,
                        y1 = 194.47620779982384f,
                    )
                    close()
                    moveTo(x = 163.8340935926206f, y = 198.4053814448534f)
                    lineTo(x = 164.28768971404617f, y = 199.29658880491485f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 160.67899527073257f,
                        y1 = 201.0211152208417f,
                    )
                    lineTo(x = 160.27050782984838f, y = 200.1083512805812f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 163.8340935926206f,
                        y1 = 198.4053814448534f,
                    )
                    close()
                    moveTo(x = 156.62626260365718f, y = 201.6310877914109f)
                    lineTo(x = 156.98862035813386f, y = 202.56312687737812f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 153.22578899162147f,
                        y1 = 203.9187695484469f,
                    )
                    lineTo(x = 152.9104666292262f, y = 202.9697849290913f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 156.62626260365718f,
                        y1 = 201.6310877914109f,
                    )
                    close()
                    moveTo(x = 149.13240746134238f, y = 204.12109664795827f)
                    lineTo(x = 149.39990628996696f, y = 205.08465483337545f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 145.52053496744327f,
                        y1 = 206.05786862613274f,
                    )
                    lineTo(x = 145.30152828035025f, y = 205.08214526830608f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 149.13240746134238f,
                        y1 = 204.12109664795827f,
                    )
                    close()
                    moveTo(x = 141.42740428911898f, y = 205.85052866908836f)
                    lineTo(x = 141.59737143201923f, y = 206.83597839907682f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 137.64022154938928f,
                        y1 = 207.41703928300709f,
                    )
                    lineTo(x = 137.51971878002192f, y = 206.4243262919695f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 141.42740428911898f,
                        y1 = 205.85052866908836f,
                    )
                    close()
                    moveTo(x = 133.58823893174846f, y = 206.8021039417203f)
                    lineTo(x = 133.6589761334162f, y = 207.79959892832437f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 129.66358622424735f,
                        y1 = 207.98270113514855f,
                    )
                    lineTo(x = 129.64279139644424f, y = 206.9829173709592f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 133.58823893174846f,
                        y1 = 206.8021039417203f,
                    )
                    close()
                    moveTo(x = 125.69323773819814f, y = 206.9663146402789f)
                    lineTo(x = 125.66403821589684f, y = 207.96588824332042f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 121.67032889546122f,
                        y1 = 207.7492022763135f,
                    )
                    lineTo(x = 121.74944978426795f, y = 206.7523372478596f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 125.69323773819814f,
                        y1 = 206.9663146402789f,
                    )
                    close()
                    moveTo(x = 117.82128495065348f, y = 206.341520025745f)
                    lineTo(x = 117.69244045635796f, y = 207.33318483619746f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 113.74031554804057f,
                        y1 = 206.71887574991493f,
                    )
                    lineTo(x = 113.91856160369005f, y = 205.734889803041f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 117.82128495065348f,
                        y1 = 206.341520025745f,
                    )
                    close()
                    moveTo(x = 110.05103451924606f, y = 204.9339628393774f)
                    lineTo(x = 109.82383242455296f, y = 205.90781047025558f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 105.9527802540389f,
                        y1 = 204.90201623802398f,
                    )
                    lineTo(x = 106.2283705008634f, y = 203.9407410350487f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 110.05103451924606f,
                        y1 = 204.9339628393774f,
                    )
                    close()
                    moveTo(x = 102.46012421778315f, y = 202.75770692730572f)
                    lineTo(x = 102.13683465091964f, y = 203.70400701499312f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 98.38553349189696f,
                        y1 = 202.3167772003095f,
                    )
                    lineTo(x = 98.75571432324826f, y = 201.38781748530565f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 102.46012421778315f,
                        y1 = 202.75770692730572f,
                    )
                    close()
                    moveTo(x = 95.12439991277569f, y = 199.8344967192288f)
                    lineTo(x = 94.70825307622854f, y = 200.7437941460545f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 91.11418468986292f,
                        y1 = 198.98898949067f,
                    )
                    lineTo(x = 91.57525738123962f, y = 198.10162712203663f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 95.12439991277569f,
                        y1 = 199.8344967192288f,
                    )
                    close()
                    moveTo(x = 88.11715773661123f, y = 196.193539965261f)
                    lineTo(x = 87.61231163201137f, y = 197.05674933190988f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 84.21138676157828f,
                        y1 = 194.95190326387979f,
                    )
                    lineTo(x = 84.75874442705856f, y = 194.11500447308129f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 88.11715773661123f,
                        y1 = 196.193539965261f,
                    )
                    close()
                    moveTo(x = 81.50841173682768f, y = 191.87121590174763f)
                    lineTo(x = 80.91991061957233f, y = 192.6797123055672f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 77.74611018218087f,
                        y1 = 190.2458557510337f,
                    )
                    lineTo(x = 78.3742838049036f, y = 189.46778255414577f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 81.50841173682768f,
                        y1 = 191.87121590174763f,
                    )
                    close()
                    moveTo(x = 75.3641943188939f, y = 186.9107117619609f)
                    lineTo(x = 74.69791829761408f, y = 187.6564169741376f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 71.7829538581157f,
                        y1 = 184.9178682232676f,
                    )
                    lineTo(x = 72.48566693488925f, y = 184.20639487047674f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 75.3641943188939f,
                        y1 = 186.9107117619609f,
                    )
                    close()
                    moveTo(x = 69.74589647224164f, y = 181.36159126354096f)
                    lineTo(x = 69.0085027567004f, y = 182.03705444409212f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 66.38149967621545f,
                        y1 = 179.02117617076033f,
                    )
                    lineTo(x = 67.15173093026276f, y = 178.38341146862584f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 69.74589647224164f,
                        y1 = 181.36159126354096f,
                    )
                    close()
                    moveTo(x = 64.70965437179228f, y = 175.2792993842126f)
                    lineTo(x = 63.90851075624535f, y = 175.87777152831657f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 61.595717181182266f,
                        y1 = 172.61469739131343f,
                    )
                    lineTo(x = 62.42577071641749f, y = 172.057013673922f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 64.70965437179228f,
                        y1 = 175.2792993842126f,
                    )
                    close()
                    moveTo(x = 60.30578848385322f, y = 168.72460837389576f)
                    lineTo(x = 59.44889973048427f, y = 169.24010974571723f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 57.473424329737185f,
                        y1 = 165.7624433031907f,
                    )
                    lineTo(x = 58.355006525615465f, y = 165.29041276190082f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 60.30578848385322f,
                        y1 = 168.72460837389576f,
                    )
                    close()
                    moveTo(x = 56.57830078065223f, y = 161.76301053847268f)
                    lineTo(x = 55.67422863863517f, y = 162.1903904187065f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 54.055809709402965f,
                        y1 = 158.53287936418667f,
                    )
                    lineTo(x = 54.980112088035426f, y = 158.15121837213434f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 56.57830078065223f,
                        y1 = 161.76301053847268f,
                    )
                    close()
                    moveTo(x = 53.56443508717605f, y = 154.46406386231664f)
                    lineTo(x = 52.6222127465074f, y = 154.79905201247254f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 51.37702099575283f,
                        y1 = 150.99824098740373f,
                    )
                    lineTo(x = 52.33480823330591f, y = 150.7107629750612f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 53.56443508717605f,
                        y1 = 154.46406386231664f,
                    )
                    close()
                    moveTo(x = 51.29430495318239f, y = 146.9006970079048f)
                    lineTo(x = 50.32334678803281f, y = 147.13994633711877f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 49.46382376012372f,
                        y1 = 143.23381178888238f,
                    )
                    lineTo(x = 50.445525963122165f, y = 143.04338914152135f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 51.29430495318239f,
                        y1 = 146.9006970079048f,
                    )
                    close()
                    moveTo(x = 49.790592768564835f, y = 139.14848063672972f)
                    lineTo(x = 48.8006002719644f, y = 139.28960064478957f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 48.335334036875125f,
                        y1 = 135.3171713785952f,
                    )
                    lineTo(x = 49.33114236141418f, y = 135.22570673636275f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 49.790592768564835f,
                        y1 = 139.14848063672972f,
                    )
                    close()
                    moveTo(x = 49.068323128410924f, y = 131.2848723322302f)
                    lineTo(x = 48.06918797813765f, y = 131.3264529946635f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 48.002827322292646f,
                        y1 = 127.32742021062836f,
                    )
                    lineTo(x = 49.00279198076399f, y = 127.3358274579955f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 49.068323128410924f,
                        y1 = 131.2848723322302f,
                    )
                    close()
                    moveTo(x = 49.134712712214494f, y = 123.38844266922145f)
                    lineTo(x = 48.13641793641975f, y = 123.33006852579388f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 48.46962591355627f,
                        y1 = 119.34438923759161f,
                    )
                    lineTo(x = 49.46375558963682f, y = 119.45258437212172f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 49.134712712214494f,
                        y1 = 123.38844266922145f,
                    )
                    close()
                    moveTo(x = 49.98909817719964f, y = 115.53809016268367f)
                    lineTo(x = 49.00161840729076f, y = 115.38034446854043f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 49.731065713445204f,
                        y1 = 111.44784226612833f,
                    )
                    lineTo(x = 50.70942739202714f, y = 111.65474423780172f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 49.98909817719964f,
                        y1 = 115.53809016268367f,
                    )
                    close()
                    moveTo(x = 51.622942786222495f, y = 107.81225293988064f)
                    lineTo(x = 50.656144593643035f, y = 107.55671183785381f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 51.77454283245552f,
                        y1 = 103.71667898332599f,
                    )
                    lineTo(x = 52.727361047049826f, y = 104.02022049603441f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 51.622942786222495f,
                        y1 = 107.81225293988064f,
                    )
                    close()
                    moveTo(x = 54.01992170402697f, y = 100.28812501252037f)
                    lineTo(x = 53.08346501673617f, y = 99.93734178483075f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 54.57963952269779f,
                        y1 = 96.22814661712354f,
                    )
                    lineTo(x = 55.49739402866406f, y = 96.6252947844095f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 54.01992170402697f,
                        y1 = 100.28812501252037f,
                    )
                    close()
                    moveTo(x = 57.156085109602216f, y = 93.040884979707f)
                    lineTo(x = 56.259326693268065f, y = 92.59836453641216f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 58.11832818528478f,
                        y1 = 89.05706810754438f,
                    )
                    lineTo(x = 58.99184908296871f, y = 89.54385475620009f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 57.156085109602216f,
                        y1 = 93.040884979707f,
                    )
                    close()
                    moveTo(x = 61.00009749487754f, y = 86.14294486822938f)
                    lineTo(x = 60.15199746316712f, y = 85.61310872732089f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 62.3552514128349f,
                        y1 = 82.27509450061285f,
                    )
                    lineTo(x = 63.175810770174465f, y = 82.84665581935519f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 61.00009749487754f,
                        y1 = 86.14294486822938f,
                    )
                    close()
                    moveTo(x = 65.51355075876081f, y = 79.66322661552553f)
                    lineTo(x = 64.7225830468464f, y = 79.05136872458283f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 67.24807526899907f,
                        y1 = 75.94998903478697f,
                    )
                    lineTo(x = 68.00747432813658f, y = 76.60061417185213f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 65.51355075876081f,
                        y1 = 79.66322661552553f,
                    )
                    close()
                    moveTo(x = 70.65134796818862f, y = 73.6664734244664f)
                    lineTo(x = 69.92541566398846f, y = 72.97870726528244f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 72.74791227515904f,
                        y1 = 70.14495007307937f,
                    )
                    lineTo(x = 73.43856337171954f, y = 70.86813819716588f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 70.65134796818862f,
                        y1 = 73.6664734244664f,
                    )
                    close()
                    moveTo(x = 76.36215395177429f, y = 68.21260287067399f)
                    lineTo(x = 75.70851033091067f, y = 67.45580037536607f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 78.79980987795366f,
                        y1 = 64.91797964590471f,
                    )
                    lineTo(x = 79.41481225447924f, y = 65.7065049003309f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 76.36215395177429f,
                        y1 = 68.21260287067399f,
                    )
                    close()
                    moveTo(x = 82.58890822387133f, y = 63.35610822591187f)
                    lineTo(x = 82.01408427733806f, y = 62.537831114847464f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 85.34329951702718f,
                        y1 = 60.321303913964584f,
                    )
                    lineTo(x = 85.87650827306433f, y = 61.167287615040024f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 82.58890822387133f,
                        y1 = 63.35610822591187f,
                    )
                    close()
                    moveTo(x = 89.26939511408426f, y = 59.14551397932681f)
                    lineTo(x = 88.77913429274355f, y = 58.27393820691323f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 92.31300080689607f,
                        y1 = 56.400851341713576f,
                    )
                    lineTo(x = 92.75908829680986f, y = 57.29584069994215f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 89.26939511408426f,
                        y1 = 59.14551397932681f,
                    )
                    close()
                    moveTo(x = 96.33686540568142f, y = 55.62289099679329f)
                    lineTo(x = 95.93606623360145f, y = 54.70672506004384f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 99.63927479614891f,
                        y1 = 53.19579379532428f,
                    )
                    lineTo(x = 99.99378386119704f, y = 54.13084637288273f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 96.33686540568142f,
                        y1 = 55.62289099679329f,
                    )
                    close()
                    moveTo(x = 103.72070327170427f, y = 52.82343616272843f)
                    lineTo(x = 103.41337040172584f, y = 51.87183408883891f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 107.2489197768293f,
                        y1 = 50.73815515034906f,
                    )
                    lineTo(x = 107.50830827961893f, y = 51.70392821096969f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 103.72070327170427f,
                        y1 = 52.82343616272843f,
                    )
                    close()
                    moveTo(x = 111.34713184496779f, y = 50.775120704457464f)
                    lineTo(x = 111.136336045537f, y = 49.79759058679237f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 115.06590269170445f,
                        y1 = 49.05249131974118f,
                    )
                    lineTo(x = 115.22757890805815f, y = 50.039335178244414f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 111.34713184496779f,
                        y1 = 50.775120704457464f,
                    )
                    close()
                    moveTo(x = 119.13995037213004f, y = 49.498410712956385f)
                    lineTo(x = 119.02779784519498f, y = 48.50471970932291f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 123.01211883143996f,
                        y1 = 48.15564489928968f,
                    )
                    lineTo(x = 123.07446734604696f, y = 49.15369933804857f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 119.13995037213004f,
                        y1 = 49.498410712956385f,
                    )
                    close()
                    moveTo(x = 127.02129558643094f, y = 49.00606265243603f)
                    lineTo(x = 127.00890692296804f, y = 48.00613939487194f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 131.0081722310374f,
                        y1 = 48.05657688196973f,
                    )
                    lineTo(x = 130.97057007814945f, y = 49.055869670945114f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 127.02129558643094f,
                        y1 = 49.00606265243603f,
                    )
                    close()
                    moveTo(x = 134.91241969171557f, y = 49.30299590196853f)
                    lineTo(x = 134.99991867515502f, y = 48.30683129313269f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 138.97416896807187f,
                        y1 = 48.75627712266284f,
                    )
                    lineTo(x = 138.83699185597098f, y = 49.74682365862955f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 134.91241969171557f,
                        y1 = 49.30299590196853f,
                    )
                    close()
                    moveTo(x = 142.73447718438274f, y = 50.386243602677595f)
                    lineTo(x = 142.9209895538053f, y = 49.40379099005327f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 146.83051543635534f,
                        y1 = 50.24775444785416f,
                    )
                    lineTo(x = 146.5951339934009f, y = 51.21965751725598f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 142.73447718438274f,
                        y1 = 50.386243602677595f,
                    )
                    close()
                    moveTo(x = 150.40931265159412f, y = 52.24498230161184f)
                    lineTo(x = 150.69297483705736f, y = 51.286058026948695f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 154.49871361893955f,
                        y1 = 52.51610650912721f,
                    )
                    lineTo(x = 154.1674796987028f, y = 53.459655177763125f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 150.40931265159412f,
                        y1 = 52.24498230161184f,
                    )
                    close()
                    moveTo(x = 157.86024167432473f, y = 54.86064009610885f)
                    lineTo(x = 158.2382194170377f, y = 53.93482541378111f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 161.9021454143535f,
                        y1 = 55.538668682503285f,
                    )
                    lineTo(x = 161.47836859667407f, y = 56.44443532397199f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 157.86024167432473f,
                        y1 = 54.86064009610885f,
                    )
                    close()
                    moveTo(x = 165.01281703272906f, y = 58.207082198107514f)
                    lineTo(x = 165.48133370402942f, y = 57.32362754238736f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 168.96683817934652f,
                        y1 = 59.28524052587221f,
                    )
                    lineTo(x = 168.45475270210468f, y = 60.1441750192988f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 165.01281703272906f,
                        y1 = 58.207082198107514f,
                    )
                    close()
                    moveTo(x = 171.795572558153f, y = 62.25087206431134f)
                    lineTo(x = 172.34994689433216f, y = 61.41860462208743f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 175.6222038390878f,
                        y1 = 63.71838753182702f,
                    )
                    lineTo(x = 175.02692629109922f, y = 64.52190768767919f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 171.795572558153f,
                        y1 = 62.25087206431134f,
                    )
                    close()
                    moveTo(x = 178.14073719946742f, y = 66.95160548307643f)
                    lineTo(x = 178.77543007541004f, y = 66.17884099552045f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 181.8017441798766f,
                        y1 = 68.79381516088783f,
                    )
                    lineTo(x = 181.12922237762814f, y = 69.53389247137673f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 178.14073719946742f,
                        y1 = 66.95160548307643f,
                    )
                    close()
                    moveTo(x = 183.9849121690089f, y = 72.26231427993838f)
                    lineTo(x = 184.69358194330016f, y = 71.556773954368f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 187.44371527731218f,
                        y1 = 74.46081141789836f,
                    )
                    lineTo(x = 186.70066883634576f, y = 75.13005127517462f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 183.9849121690089f,
                        y1 = 72.26231427993838f,
                    )
                    close()
                    moveTo(x = 189.26970440230915f, y = 78.12993560808589f)
                    lineTo(x = 190.04527028081938f, y = 77.49866897021356f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 192.4917444211767f,
                        y1 = 80.66275354951844f,
                    )
                    lineTo(x = 191.68559761591195f, y = 81.25446913014946f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 189.26970440230915f,
                        y1 = 78.12993560808589f,
                    )
                    close()
                    moveTo(x = 193.94231000229308f, y = 84.4958421347858f)
                    lineTo(x = 194.77702278713224f, y = 83.94515659218816f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 196.89539337292115f,
                        y1 = 87.33767380005847f,
                    )
                    lineTo(x = 196.03420095575964f, y = 87.84595287755775f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 193.94231000229308f,
                        y1 = 84.4958421347858f,
                    )
                    close()
                    moveTo(x = 197.95604183836375f, y = 91.2964278263123f)
                    lineTo(x = 198.84156135530503f, y = 90.83182564689852f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 200.61066232787263f,
                        y1 = 94.41887857281033f,
                    )
                    lineTo(x = 199.70302904877423f, y = 94.8386425906502f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 197.95604183836375f,
                        y1 = 91.2964278263123f,
                    )
                    close()
                    moveTo(x = 201.27079602877845f, y = 98.46374347841038f)
                    lineTo(x = 202.19827445952248f, y = 98.08986681358013f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 203.60042954673784f,
                        y1 = 101.83561481041974f,
                    )
                    lineTo(x = 202.65542417740363f, y = 102.1626696252895f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 201.27079602877845f,
                        y1 = 98.46374347841038f,
                    )
                    close()
                    moveTo(x = 203.85345264537864f, y = 105.92617564228385f)
                    lineTo(x = 204.81362293202898f, y = 105.64676014408491f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 205.83482226375156f,
                        y1 = 109.51377693604758f,
                    )
                    lineTo(x = 204.86188698545465f, y = 109.744854724347f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 203.85345264537864f,
                        y1 = 105.92617564228385f,
                    )
                    close()
                    moveTo(x = 205.67820663696398f, y = 113.6091621625034f)
                    lineTo(x = 206.66147507540657f, y = 113.42699965823128f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 207.29151516547736f,
                        y1 = 117.37664729179754f,
                    )
                    lineTo(x = 206.30037122590892f, y = 117.50943920065006f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 205.67820663696398f,
                        y1 = 113.6091621625034f,
                    )
                    close()
                    moveTo(x = 206.72682566483408f, y = 121.43593717741666f)
                    lineTo(x = 207.7233677618573f, y = 121.35284777459916f)
                    arcTo(
                        horizontalEllipseRadius = 80.0f,
                        verticalEllipseRadius = 80.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 207.95595345795994f,
                        y1 = 125.34566267619432f,
                    )
                    lineTo(x = 206.95650403973542f, y = 125.37884189274189f)
                    arcTo(
                        horizontalEllipseRadius = 79.0f,
                        verticalEllipseRadius = 79.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 206.72682566483408f,
                        y1 = 121.43593717741666f,
                    )
                    close()
                }
            }
            group {
                path {
                    moveTo(x = 128.0f, y = 128.0f)
                    moveToRelative(dx = -59.5f, dy = 0.0f)
                    arcToRelative(
                        a = 59.5f,
                        b = 59.5f,
                        theta = 0.0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = 119.0f,
                        dy1 = 0.0f,
                    )
                    arcToRelative(
                        a = 59.5f,
                        b = 59.5f,
                        theta = 0.0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = -119.0f,
                        dy1 = 0.0f,
                    )
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF2563EB)),
                    fillAlpha = 0.3f,
                ) {
                    moveTo(x = 187.0f, y = 128.0f)
                    lineTo(x = 188.0f, y = 128.0f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 187.86671604206734f,
                        y1 = 131.99703769540358f,
                    )
                    lineTo(x = 186.86893744136623f, y = 131.9304204004802f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 187.0f,
                        y1 = 128.0f,
                    )
                    close()
                    moveTo(x = 186.4763320500166f, y = 135.84337873471654f)
                    lineTo(x = 187.46745632205077f, y = 135.97631735733884f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 186.80399467047448f,
                        y1 = 139.92015984770367f,
                    )
                    lineTo(x = 185.82392809263325f, y = 139.7214905169086f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 186.4763320500166f,
                        y1 = 135.84337873471654f,
                    )
                    close()
                    moveTo(x = 184.91462406860327f, y = 143.54752607746858f)
                    lineTo(x = 185.87927871383383f, y = 143.8110434686121f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 184.69741677888425f,
                        y1 = 147.63168180776913f,
                    )
                    lineTo(x = 183.7524598325695f, y = 147.30448711097299f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 184.91462406860327f,
                        y1 = 143.54752607746858f,
                    )
                    close()
                    moveTo(x = 182.34259864617022f, y = 150.97568219621039f)
                    lineTo(x = 183.2636596401731f, y = 151.36510053851902f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 181.58437719190576f,
                        y1 = 154.99471283335998f,
                    )
                    lineTo(x = 180.69130423870735f, y = 154.54480095280397f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 182.34259864617022f,
                        y1 = 150.97568219621039f,
                    )
                    close()
                    moveTo(x = 178.8059129786588f, y = 157.99598650494676f)
                    lineTo(x = 179.6670301477886f, y = 158.50439305587807f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 177.5201368945807f,
                        y1 = 161.87854840370213f,
                    )
                    lineTo(x = 176.69480127967103f, y = 161.3139059303071f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 178.8059129786588f,
                        y1 = 157.99598650494676f,
                    )
                    close()
                    moveTo(x = 174.36734838583993f, y = 164.48381838111447f)
                    lineTo(x = 175.15323564661688f, y = 165.10218818418423f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 172.57684206944265f,
                        y1 = 168.16099041502798f,
                    )
                    lineTo(x = 171.8338947016186f, y = 167.4916405747775f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 174.36734838583993f,
                        y1 = 164.48381838111447f,
                    )
                    close()
                    moveTo(x = 169.10569585148278f, y = 170.32400936307184f)
                    lineTo(x = 169.80240256082993f, y = 171.04136545397137f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 166.84224339673716f,
                        y1 = 173.7305163748304f,
                    )
                    lineTo(x = 166.1948726734582f, y = 172.96834110191654f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 169.10569585148278f,
                        y1 = 170.32400936307184f,
                    )
                    close()
                    moveTo(x = 163.11435736867855f, y = 175.41288755797024f)
                    lineTo(x = 163.70951596814768f, y = 176.21649582166467f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 160.4181383520884f,
                        y1 = 178.4882590884738f,
                    )
                    lineTo(x = 159.87783604622024f, y = 177.64678810366587f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 163.11435736867855f,
                        y1 = 175.41288755797024f,
                    )
                    close()
                    moveTo(x = 156.49968791847633f, y = 179.6601179687915f)
                    lineTo(x = 156.9827334764166f, y = 180.53571318860153f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 153.41856415309667f,
                        y1 = 182.34976169584286f,
                    )
                    lineTo(x = 152.99492141721174f, y = 181.4439323342455f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 156.49968791847633f,
                        y1 = 179.6601179687915f,
                    )
                    close()
                    moveTo(x = 149.37910751412375f, y = 182.99030607206635f)
                    lineTo(x = 149.74146526860042f, y = 183.92234515803358f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 145.96777343269568f,
                        y1 = 185.24647690357298f,
                    )
                    lineTo(x = 145.66831054215075f, y = 184.2923689551801f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 149.37910751412375f,
                        y1 = 182.99030607206635f,
                    )
                    close()
                    moveTo(x = 141.87901682487637f, y = 185.34433618043545f)
                    lineTo(x = 142.11425439817936f, y = 186.31627408179875f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 138.19802857401447f,
                        y1 = 187.1269837993076f,
                    )
                    lineTo(x = 138.02806143111422f, y = 186.14153406931916f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 141.87901682487637f,
                        y1 = 185.34433618043545f,
                    )
                    close()
                    moveTo(x = 134.1325533800877f, y = 186.68042083216832f)
                    lineTo(x = 134.23649496280106f, y = 187.67500423610338f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 130.24725386051202f,
                        y1 = 187.95790064775795f,
                    )
                    lineTo(x = 130.2097996295035f, y = 186.95860230362865f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 134.1325533800877f,
                        y1 = 186.68042083216832f,
                    )
                    close()
                    moveTo(x = 126.27722818422397f, y = 186.97484257944882f)
                    lineTo(x = 126.24802866192269f, y = 187.9744161824903f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 122.25658711913748f,
                        y1 = 187.7244774651059f,
                    )
                    lineTo(x = 122.35231066715185f, y = 186.72906950735413f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 126.27722818422397f,
                        y1 = 186.97484257944882f,
                    )
                    close()
                    moveTo(x = 118.45248469632827f, y = 186.2223750076047f)
                    lineTo(x = 118.29066240304569f, y = 187.20919492298785f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 114.36787431841479f,
                        y1 = 186.4308578526917f,
                    )
                    lineTo(x = 114.59507641310788f, y = 185.4570102218135f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 118.45248469632827f,
                        y1 = 186.2223750076047f,
                    )
                    close()
                    moveTo(x = 110.79722350560235f, y = 184.43637551157764f)
                    lineTo(x = 110.50565102264646f, y = 185.392924249062f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 106.72115159598336f,
                        y1 = 184.10000544205747f,
                    )
                    lineTo(x = 107.07579906938363f, y = 183.1650053513565f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 110.79722350560235f,
                        y1 = 184.43637551157764f,
                    )
                    close()
                    moveTo(x = 103.44733664371861f, y = 181.64854818271522f)
                    lineTo(x = 103.03118980717147f, y = 182.55784560954092f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 99.4521594132719f,
                        y1 = 180.77329625705372f,
                    )
                    lineTo(x = 99.92795675638403f, y = 179.89374131943617f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 103.44733664371861f,
                        y1 = 181.64854818271522f,
                    )
                    close()
                    moveTo(x = 96.53329530340831f, y = 177.90838101499082f)
                    lineTo(x = 95.99996132549998f, y = 178.75428577795674f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 92.68993296467926f,
                        y1 = 176.5097842291754f,
                    )
                    lineTo(x = 93.27843408193459f, y = 175.70128782535582f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 96.53329530340831f,
                        y1 = 177.90838101499082f,
                    )
                    close()
                    moveTo(x = 90.17783378483352f, y = 173.2822674210592f)
                    lineTo(x = 89.53678012016968f, y = 174.04976347904326f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 86.55451161500739f,
                        y1 = 171.38515290429945f,
                    )
                    lineTo(x = 87.24526975475727f, y = 170.66206702256113f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 90.17783378483352f,
                        y1 = 173.2822674210592f,
                    )
                    close()
                    moveTo(x = 84.49377078306648f, y = 167.85232765251786f)
                    lineTo(x = 83.75637706752525f, y = 168.52779083306902f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 81.1548080259671f,
                        y1 = 165.4903719495553f,
                    )
                    lineTo(x = 81.93556122553431f, y = 164.86553241706272f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 84.49377078306648f,
                        y1 = 167.85232765251786f,
                    )
                    close()
                    moveTo(x = 79.58200669199573f, y = 161.71495104587953f)
                    lineTo(x = 78.76136273762278f, y = 162.2863908941148f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 76.58667479786311f,
                        y1 = 158.9300823092878f,
                    )
                    lineTo(x = 77.44356355123207f, y = 158.41458093746633f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 79.58200669199573f,
                        y1 = 161.71495104587953f,
                    )
                    close()
                    moveTo(x = 75.5297324754305f, y = 154.9790849715128f)
                    lineTo(x = 74.64040590721747f, y = 155.43635759814862f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 72.93120294638402f,
                        y1 = 151.82073867594488f,
                    )
                    lineTo(x = 73.84901623061097f, y = 151.42372636467914f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 75.5297324754305f,
                        y1 = 154.9790849715128f,
                    )
                    close()
                    moveTo(x = 72.40888190054912f, y = 147.76430085919827f)
                    lineTo(x = 71.46665955988047f, y = 148.09928900935418f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 70.2532824190107f,
                        y1 = 144.28854224973682f,
                    )
                    lineTo(x = 71.21572771202719f, y = 144.01706654557455f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 72.40888190054912f,
                        y1 = 147.76430085919827f,
                    )
                    close()
                    moveTo(x = 70.27485460880082f, y = 140.19867163116197f)
                    lineTo(x = 69.29646231403473f, y = 140.40542877745287f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 68.60045020397325f,
                        y1 = 136.46720048359188f,
                    )
                    lineTo(x = 69.5904427005737f, y = 136.32608047553202f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 70.27485460880082f,
                        y1 = 140.19867163116197f,
                    )
                    close()
                    moveTo(x = 69.16553269161197f, y = 132.41649822124054f)
                    lineTo(x = 68.16833833045285f, y = 132.49135412329548f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 68.0020464794993f,
                        y1 = 128.49555358119807f,
                    )
                    lineTo(x = 69.00201237150765f, y = 128.48729435484478f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 69.16553269161197f,
                        y1 = 132.41649822124054f,
                    )
                    close()
                    moveTo(x = 69.10060822810958f, y = 124.5559255377726f)
                    lineTo(x = 68.10231345231483f, y = 124.49755139434501f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 68.4686937829332f,
                        y1 = 120.51510988124586f,
                    )
                    lineTo(x = 69.46088221988431f, y = 120.63985804989176f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 69.10060822810958f,
                        y1 = 124.5559255377726f,
                    )
                    close()
                    moveTo(x = 70.08123372204638f, y = 116.75649019034616f)
                    lineTo(x = 69.0995597173353f, y = 116.56592222747067f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 69.9921084452324f,
                        y1 = 112.66753387838989f,
                    )
                    lineTo(x = 70.95890663781186f, y = 112.92307498041673f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 70.08123372204638f,
                        y1 = 116.75649019034616f,
                    )
                    close()
                    moveTo(x = 72.0900016431813f, y = 109.15664351182278f)
                    lineTo(x = 71.14237455238776f, y = 108.83726458829435f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 72.54524763815348f,
                        y1 = 105.09213147221507f,
                    )
                    lineTo(x = 73.46949351085092f, y = 105.4739292810115f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 72.0900016431813f,
                        y1 = 109.15664351182278f,
                    )
                    close()
                    moveTo(x = 75.09125343628546f, y = 101.89129384560347f)
                    lineTo(x = 74.19449501995129f, y = 101.44877340230862f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 76.082789423826f,
                        y1 = 97.92337708469907f,
                    )
                    lineTo(x = 76.94807626676223f, y = 98.42465413328742f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 75.09125343628546f,
                        y1 = 101.89129384560347f,
                    )
                    close()
                    moveTo(x = 79.03171251241375f, y = 95.08941172611611f)
                    lineTo(x = 78.20174153804788f, y = 94.53160514520283f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 80.54193728513519f,
                        y1 = 91.2885265434366f,
                    )
                    lineTo(x = 81.33290499704961f, y = 91.90038443437933f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 79.03171251241375f,
                        y1 = 95.08941172611611f,
                    )
                    close()
                    moveTo(x = 83.84142998592588f, y = 88.871740464057f)
                    lineTo(x = 83.0929796467043f, y = 88.20854962446475f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 85.84353485532839f,
                        y1 = 85.30535810542398f,
                    )
                    lineTo(x = 86.54614260773957f, y = 86.01693547033356f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 83.84142998592588f,
                        y1 = 88.871740464057f,
                    )
                    close()
                    moveTo(x = 89.43502636904714f, y = 83.34865277683203f)
                    lineTo(x = 88.78138274818353f, y = 82.5918502815241f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 91.89347105877096f,
                        y1 = 80.08008172152013f,
                    )
                    lineTo(x = 92.49524654112477f, y = 80.87874702616146f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 89.43502636904714f,
                        y1 = 83.34865277683203f,
                    )
                    close()
                    moveTo(x = 95.71320718208312f, y = 78.61819151212754f)
                    lineTo(x = 95.16597340550827f, y = 77.78121170724836f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 98.58435071955827f,
                        y1 = 75.70545365518458f,
                    )
                    lineTo(x = 99.07461154089896f, y = 76.57702942759818f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 95.71320718208312f,
                        y1 = 78.61819151212754f,
                    )
                    close()
                    moveTo(x = 102.56452557500042f, y = 74.76432924462028f)
                    lineTo(x = 102.13341583898347f, y = 73.8620297402918f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 105.79740098224796f,
                        y1 = 72.25912992375419f,
                    )
                    lineTo(x = 106.1674442992105f, y = 73.18814442502494f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 102.56452557500042f,
                        y1 = 74.76432924462028f,
                    )
                    close()
                    moveTo(x = 109.86736067127346f, y = 71.85547764051849f)
                    lineTo(x = 109.56002780129504f, y = 70.90387556662898f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 113.40457970296917f,
                        y1 = 69.80228779107361f,
                    )
                    lineTo(x = 113.6478367079197f, y = 70.77224966122238f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 109.86736067127346f,
                        y1 = 71.85547764051849f,
                    )
                    close()
                    moveTo(x = 117.49207651551474f, y = 69.94327305088407f)
                    lineTo(x = 117.3139761174726f, y = 68.95926072971261f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 121.27084838389692f,
                        y1 = 68.37853978199212f,
                    )
                    lineTo(x = 121.38300091083197f, y = 69.37223078562558f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 117.49207651551474f,
                        y1 = 69.94327305088407f,
                    )
                    close()
                    moveTo(x = 125.30332330042391f, y = 69.06165989122222f)
                    lineTo(x = 125.25761691568533f, y = 68.06270497412429f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 129.25656930319525f,
                        y1 = 68.01315949655068f,
                    )
                    lineTo(x = 129.23562648147532f, y = 69.01294017160816f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 125.30332330042391f,
                        y1 = 69.06165989122222f,
                    )
                    close()
                    moveTo(x = 133.16244002292748f, y = 69.22628807868541f)
                    lineTo(x = 133.24993900636693f, y = 68.23012346984957f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 137.21998428788285f,
                        y1 = 68.71263296678461f,
                    )
                    lineTo(x = 137.0663178830848f, y = 69.70075575067155f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 133.16244002292748f,
                        y1 = 69.22628807868541f,
                    )
                    close()
                    moveTo(x = 140.92991591886434f, y = 70.4342352232588f)
                    lineTo(x = 141.14906703613323f, y = 69.45854429483946f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 145.01973112779368f,
                        y1 = 70.46454352021172f,
                    )
                    lineTo(x = 144.73606894233046f, y = 71.42346779487485f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 140.92991591886434f,
                        y1 = 70.4342352232588f,
                    )
                    close()
                    moveTo(x = 148.46786698216764f, y = 72.66405850443772f)
                    lineTo(x = 148.8147799818654f, y = 71.7261611909536f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 152.5173529606211f,
                        y1 = 73.23779219384664f,
                    )
                    lineTo(x = 152.1087304112774f, y = 74.15049565728253f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 148.46786698216764f,
                        y1 = 72.66405850443772f,
                    )
                    close()
                    moveTo(x = 155.6424836067223f, y = 75.87617531251098f)
                    lineTo(x = 156.1110002780227f, y = 74.99272065679082f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 159.57975608280756f,
                        y1 = 76.98314978607186f,
                    )
                    lineTo(x = 159.05342681476077f, y = 77.83343062297067f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 155.6424836067223f,
                        y1 = 75.87617531251098f,
                    )
                    close()
                    moveTo(x = 162.3264059016911f, y = 80.0135658975128f)
                    lineTo(x = 162.9082093915503f, y = 79.20023650594523f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 166.0815725565581f,
                        y1 = 81.63413074664078f,
                    )
                    lineTo(x = 165.44687968061544f, y = 82.40689523419675f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 162.3264059016911f,
                        y1 = 80.0135658975128f,
                    )
                    close()
                    moveTo(x = 168.40098451500444f, y = 85.00278555280153f)
                    lineTo(x = 169.08574696441127f, y = 84.27401920623885f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 171.90738567326326f,
                        y1 = 87.10817339199292f,
                    )
                    lineTo(x = 171.1755959120422f, y = 87.7897038354597f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 168.40098451500444f,
                        y1 = 85.00278555280153f,
                    )
                    close()
                    moveTo(x = 173.75838683210472f, y = 90.75526836553303f)
                    lineTo(x = 174.53395271061498f, y = 90.1240017276607f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 176.95377876811597f,
                        y1 = 93.30810549534142f,
                    )
                    lineTo(x = 176.137882455314f, y = 93.88630373708574f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 173.75838683210472f,
                        y1 = 90.75526836553303f,
                    )
                    close()
                    moveTo(x = 178.30351116185193f, y = 97.16889939055956f)
                    lineTo(x = 179.15611304595112f, y = 96.6463383632809f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 181.1311710164791f,
                        y1 = 100.12386923517451f,
                    )
                    lineTo(x = 180.2456514995378f, y = 100.58847141458827f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 178.30351116185193f,
                        y1 = 97.16889939055956f,
                    )
                    close()
                    moveTo(x = 181.9556749300027f, y = 104.12982733937858f)
                    lineTo(x = 182.870177894918f, y = 103.72524814174092f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 184.36540762394674f,
                        y1 = 107.43447488182406f,
                    )
                    lineTo(x = 183.42598416354764f, y = 107.77723363379367f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 181.9556749300027f,
                        y1 = 104.12982733937858f,
                    )
                    close()
                    moveTo(x = 184.65004691237158f, y = 111.51448560626326f)
                    lineTo(x = 185.61021719902192f, y = 111.23507010806435f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 186.5990761819089f,
                        y1 = 115.11014854131999f,
                    )
                    lineTo(x = 185.62242491221042f, y = 115.32497939896464f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 184.65004691237158f,
                        y1 = 111.51448560626326f,
                    )
                    close()
                    moveTo(x = 186.33879808345006f, y = 119.19178575541882f)
                    lineTo(x = 187.32759127130515f, y = 119.04249398856152f)
                    arcTo(
                        horizontalEllipseRadius = 60.0f,
                        verticalEllipseRadius = 60.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 187.79252582139304f,
                        y1 = 123.01463583095006f,
                    )
                    lineTo(x = 186.79598372436982f, y = 123.09772523376756f)
                    arcTo(
                        horizontalEllipseRadius = 59.0f,
                        verticalEllipseRadius = 59.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 186.33879808345006f,
                        y1 = 119.19178575541882f,
                    )
                    close()
                }
            }
            path(
                fill = SolidColor(Color(0xFF2563EB)),
            ) {
                moveTo(x = 128.0f, y = 140.0f)
                curveTo(
                    x1 = 123.582f,
                    y1 = 140.0f,
                    x2 = 120.0f,
                    y2 = 136.418f,
                    x3 = 120.0f,
                    y3 = 132.0f,
                )
                curveTo(
                    x1 = 120.0f,
                    y1 = 127.582f,
                    x2 = 123.582f,
                    y2 = 124.0f,
                    x3 = 128.0f,
                    y3 = 124.0f,
                )
                curveTo(
                    x1 = 132.418f,
                    y1 = 124.0f,
                    x2 = 136.0f,
                    y2 = 127.582f,
                    x3 = 136.0f,
                    y3 = 132.0f,
                )
                curveTo(
                    x1 = 136.0f,
                    y1 = 136.418f,
                    x2 = 132.418f,
                    y2 = 140.0f,
                    x3 = 128.0f,
                    y3 = 140.0f,
                )
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF2563EB)),
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 2.0f,
            ) {
                moveTo(x = 112.5f, y = 116.5f)
                curveTo(
                    x1 = 117.113f,
                    y1 = 111.887f,
                    x2 = 122.557f,
                    y2 = 110.0f,
                    x3 = 128.0f,
                    y3 = 110.0f,
                )
                curveTo(
                    x1 = 133.443f,
                    y1 = 110.0f,
                    x2 = 138.887f,
                    y2 = 111.887f,
                    x3 = 143.5f,
                    y3 = 116.5f,
                )
            }
            path(
                stroke = SolidColor(Color(0xFF2563EB)),
                strokeAlpha = 0.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 2.0f,
            ) {
                moveTo(x = 101.5f, y = 105.5f)
                curveTo(
                    x1 = 108.902f,
                    y1 = 98.098f,
                    x2 = 118.451f,
                    y2 = 95.0f,
                    x3 = 128.0f,
                    y3 = 95.0f,
                )
                curveTo(
                    x1 = 137.549f,
                    y1 = 95.0f,
                    x2 = 147.098f,
                    y2 = 98.098f,
                    x3 = 154.5f,
                    y3 = 105.5f,
                )
            }
            path(
                fill = SolidColor(Color(0xFF2563EB)),
            ) {
                moveTo(x = 52.0f, y = 104.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 12.0f,
                    dy1 = -12.0f,
                )
                lineToRelative(dx = 24.0f, dy = 0.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 12.0f,
                    dy1 = 12.0f,
                )
                lineToRelative(dx = 0.0f, dy = 24.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -12.0f,
                    dy1 = 12.0f,
                )
                lineToRelative(dx = -24.0f, dy = 0.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -12.0f,
                    dy1 = -12.0f,
                )
                close()
            }
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineWidth = 2.0f,
            ) {
                moveTo(x = 66.0f, y = 112.0f)
                lineTo(x = 86.0f, y = 112.0f)
                moveTo(x = 66.0f, y = 120.0f)
                lineTo(x = 86.0f, y = 120.0f)
                moveTo(x = 64.0f, y = 108.0f)
                lineTo(x = 88.0f, y = 108.0f)
                lineTo(x = 88.0f, y = 122.0f)
                curveTo(
                    x1 = 88.0f,
                    y1 = 123.1046f,
                    x2 = 87.1046f,
                    y2 = 124.0f,
                    x3 = 86.0f,
                    y3 = 124.0f,
                )
                lineTo(x = 66.0f, y = 124.0f)
                curveTo(
                    x1 = 64.8954f,
                    y1 = 124.0f,
                    x2 = 64.0f,
                    y2 = 123.1046f,
                    x3 = 64.0f,
                    y3 = 122.0f,
                )
                lineTo(x = 64.0f, y = 108.0f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 2.0f,
            ) {
                moveTo(x = 70.0f, y = 128.0f)
                lineTo(x = 82.0f, y = 128.0f)
                moveTo(x = 76.0f, y = 124.0f)
                lineTo(x = 76.0f, y = 128.0f)
            }
            path(
                fill = SolidColor(Color(0xFF2563EB)),
            ) {
                moveTo(x = 156.0f, y = 104.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 12.0f,
                    dy1 = -12.0f,
                )
                lineToRelative(dx = 24.0f, dy = 0.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 12.0f,
                    dy1 = 12.0f,
                )
                lineToRelative(dx = 0.0f, dy = 24.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -12.0f,
                    dy1 = 12.0f,
                )
                lineToRelative(dx = -24.0f, dy = 0.0f)
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -12.0f,
                    dy1 = -12.0f,
                )
                close()
            }
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineCap = StrokeCap.Round,
                strokeLineWidth = 2.0f,
            ) {
                moveTo(x = 176.0f, y = 108.0f)
                lineTo(x = 184.0f, y = 108.0f)
                curveTo(
                    x1 = 185.1046f,
                    y1 = 108.0f,
                    x2 = 186.0f,
                    y2 = 108.8954f,
                    x3 = 186.0f,
                    y3 = 110.0f,
                )
                lineTo(x = 186.0f, y = 124.0f)
                curveTo(
                    x1 = 186.0f,
                    y1 = 125.1046f,
                    x2 = 185.1046f,
                    y2 = 126.0f,
                    x3 = 184.0f,
                    y3 = 126.0f,
                )
                lineTo(x = 176.0f, y = 126.0f)
                curveTo(
                    x1 = 174.8954f,
                    y1 = 126.0f,
                    x2 = 174.0f,
                    y2 = 125.1046f,
                    x3 = 174.0f,
                    y3 = 124.0f,
                )
                lineTo(x = 174.0f, y = 110.0f)
                curveTo(
                    x1 = 174.0f,
                    y1 = 108.8954f,
                    x2 = 174.8954f,
                    y2 = 108.0f,
                    x3 = 176.0f,
                    y3 = 108.0f,
                )
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(x = 180.0f, y = 123.0f)
                moveToRelative(dx = -1.0f, dy = 0.0f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.0f,
                    dy1 = 0.0f,
                )
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -2.0f,
                    dy1 = 0.0f,
                )
                close()
            }
        }.build().also { _localAccessNetwork = it }
    }

@Suppress("ObjectPropertyName")
private var _localAccessNetwork: ImageVector? = null

@Preview
@Composable
private fun LocalAccessNetworkPreview() {
    Icon(imageVector = ComicIcons.LocalAccessNetwork, contentDescription = null)
}
