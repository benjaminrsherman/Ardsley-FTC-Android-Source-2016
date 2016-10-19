package org.firstinspires.ftc.robotcontroller.external;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 10/18/2016.
 */
public class AutonomousData
{
    public String hardwareType;
    public String hardwareName;

    public boolean isMotor;
    public Servo servo;
    public DcMotor motor;

    public List<Double> dataValues;

    public AutonomousData(String hardwareType, String hardwareName, Servo servo)
    {
        this.hardwareType = hardwareType;
        this.hardwareName = hardwareName;
        this.servo = servo;
        isMotor = false;
        dataValues = new ArrayList<>();
    }

    public AutonomousData(String hardwareType, String hardwareName, DcMotor motor)
    {
        this.hardwareType = hardwareType;
        this.hardwareName = hardwareName;
        this.motor = motor;
        isMotor = true;
        dataValues = new ArrayList<>();
    }

    public void AddValue(double val)
    {
        dataValues.add(val);
    }
}
