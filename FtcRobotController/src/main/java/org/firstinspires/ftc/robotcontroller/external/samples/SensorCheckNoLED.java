package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by benjamin on 1/3/17.
 */

// A quick and dirty class designed to check if all sensors are working as needed
@Autonomous(name = "Sensor Check No LED", group = "Hardware Checks")
public class SensorCheckNoLED extends OpMode {

    ColorSensor csr;
    ColorSensor csl;

    @Override
    public void init() {
        csr = (ColorSensor)hardwareMap.get(Values.COLOR_SENSOR_RIGHT);
        csl = (ColorSensor)hardwareMap.get(Values.COLOR_SENSOR_LEFT);

        csr.enableLed(false);
        csl.enableLed(false);
    }

    @Override
    public void loop() {
        telemetry.addData("Right Blue", csr.blue());
        telemetry.addData("Right Red", csr.red());
        telemetry.addData("Left Blue", csl.blue());
        telemetry.addData("Left Red", csl.red());
    }
}
