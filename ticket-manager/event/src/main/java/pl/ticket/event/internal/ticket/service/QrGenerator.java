package pl.ticket.event.internal.ticket.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.stereotype.Service;
import pl.ticket.dto.QrCodeDto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrGenerator
{


    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;

    public byte[] generateQRCodeFromObject(QrCodeDto qrCodeDto) throws Exception {
        // 1. Serializowanie obiektu do JSON
        Gson gson = new Gson();
        String json = gson.toJson(qrCodeDto);

        // 2. Generowanie QR kodu z JSON
        MultiFormatWriter qrCodeWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(json, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
        BufferedImage qrImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(qrImage, "PNG", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public <T> T decodeQRCode(byte[] qrCodeData, Class<T> targetClass) throws Exception {
        // 1. Dekodowanie QR kodu
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(qrCodeData);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();

        String decodedText = reader.decode(bitmap).getText();

        // 2. Deserializacja JSON do obiektu
        Gson gson = new Gson();
        return gson.fromJson(decodedText, targetClass);
    }
}
