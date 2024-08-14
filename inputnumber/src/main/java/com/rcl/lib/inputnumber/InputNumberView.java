package com.rcl.lib.inputnumber;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.blank.input.R;

import androidx.annotation.Nullable;

public class InputNumberView extends LinearLayout {

    private InputNumListener inputNumListener;
    private float inputNumber = 0;
    private EditText edit_input;
    private float numberMin;
    private boolean isFloat;
    private float numberMax;
    private float defaultNumber;
    private LinearLayout viewPackage;
    private boolean disable;
    private Button lessBtn;
    private Button plusBtn;
    private float step;

    public InputNumberView(Context context) {
        this(context, null);
    }

    public InputNumberView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.input_number_layout, this);

        initView();

        initAttr(context, attrs);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputNumberView);
        isFloat = typedArray.getBoolean(R.styleable.InputNumberView_isFloat, false);
        numberMin = typedArray.getFloat(R.styleable.InputNumberView_numberMin, 0);
        numberMax = typedArray.getFloat(R.styleable.InputNumberView_numberMax, 10);

        defaultNumber = typedArray.getInt(R.styleable.InputNumberView_defaultNumber, 0);
        setNumber();

        disable = typedArray.getBoolean(R.styleable.InputNumberView_btnDisable, false);
        initDisable();

        step = typedArray.getFloat(R.styleable.InputNumberView_step, 1);
    }

    private void initView() {
        viewPackage = this.findViewById(R.id.viewPackage);
        lessBtn = this.findViewById(R.id.LessBtn);
        plusBtn = this.findViewById(R.id.plusBtn);
        edit_input = this.findViewById(R.id.edit_input);
        /*edit_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                try {
                    float result = Float.parseFloat(string);
                    if (result > numberMax) {
                        edit_input.setText(String.valueOf(numberMax));
                        setInputNumber(numberMax);
                    } else if (result < numberMin) {
                        edit_input.setText(String.valueOf(numberMin));
                        setInputNumber(numberMin);
                    }
                } catch (Exception e) {
                    *//*edit_input.setText(String.valueOf(defaultNumber));
                    setInputNumber(defaultNumber);*//*
                }
            }
        });*/

        //减号事件
        lessBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //最小值
                inputNumber -= step;
                if (inputNumber <= numberMin) {
                    inputNumber = numberMin;
                }
                setInputNumber(inputNumber);
                initGetNum();
            }
        });

        //加号事件
        plusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //最大值
                inputNumber += step;
                if (inputNumber >= numberMax) {
                    inputNumber = numberMax;
                }
                setInputNumber(inputNumber);
                initGetNum();
            }
        });
    }

    private float getInputNumber() {
        return inputNumber;
    }

    private void setInputNumber(float inputNumber) {
        this.inputNumber = inputNumber;
    }

    private void initGetNum() {
        if (isFloat) {
            edit_input.setText(String.valueOf(getInputNumber()));
        } else {
            edit_input.setText(String.valueOf((int) getInputNumber()));
        }
        if (inputNumListener != null) {
            inputNumListener.inputNumber(getInputNumber());
        }
    }

    public void setInputNumListener(InputNumListener inputNumListener) {
        this.inputNumListener = inputNumListener;
    }

    public void setNumberMin(int numberMin) {
        this.numberMin = numberMin;
    }

    public void setNumberMax(int numberMax) {
        this.numberMax = numberMax;
    }

    public void setDefaultNumber(float defaultNumber) {
        this.defaultNumber = defaultNumber;
        setNumber();
    }

    private void setNumber() {
        //设置默认值
        setInputNumber(defaultNumber);
        //刷线ui
        initGetNum();
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
        initDisable();
    }

    //设置禁用状态
    private void initDisable() {
        if (disable) {
            lessBtn.setBackgroundResource(R.drawable.less_disable_bg);
            lessBtn.setEnabled(false);
            plusBtn.setBackgroundResource(R.drawable.plus_disable_bg);
            plusBtn.setEnabled(false);
            edit_input.setBackgroundResource(R.drawable.edit_disable_bg);
            edit_input.setEnabled(false);
        }
    }

    public void setStep(float step) {
        this.step = step;
    }

    public interface InputNumListener {
        //获得number
        void inputNumber(float number);
    }
}
