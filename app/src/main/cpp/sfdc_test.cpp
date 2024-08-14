#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <dlfcn.h>
#include <stdint.h>
#include <vector>
#include <string>

struct scan_config
{
    float freq_step;
    float freq_window;
};

struct app_sfdc_param
{
    float lra_f0;
    float wave_fc;
    float avg_slope;
    float bemf_offset;
    int sfdc_support;
};

struct play_record
{
    uint16_t wave_index;
    float temperature;
    float output_f0;
    float relative_bemf;
};

struct tc_wave_config
{
    uint16_t wave_index;
    int16_t f0_drift;
    uint16_t playback_count;
    float playback_interval;
};

struct tc_config
{
    char tc_name[20];
    uint16_t valid_wave_count;
    struct tc_wave_config wave_config[100];
    uint16_t repeat_loop;
    uint16_t record_interval;
};

struct tc_record
{
    uint16_t play_times;
    std::vector<play_record> play_record_list;
};

int main(int argc, char *argv[])
{
    void *dlhdl;
    int val;
    printf("hello world\n");

    printf("Number of command-line arguments: %d\n", argc);

    // 打印所有命令行参数
    printf("Command-line arguments:\n");
    for (int i = 0; i < argc; i++)
    {
        printf("argv[%d]: %s\n", i, argv[i]);
    }

    dlhdl = dlopen("/system/vendor/lib64/libics_app.so", RTLD_LAZY);
    if (dlhdl == NULL)
    {
        fprintf(stderr, "dlopen error: %s\n", dlerror());
        return -1;
    }

    // 获取函数指针
    typedef int32_t (*FunctionType)(const std::string &,
                                    const uint16_t,
                                    const std::vector<int8_t> &);
    FunctionType sfdc_wave_add = (FunctionType)dlsym(dlhdl, "sfdc_wave_add");
    if (!sfdc_wave_add)
    {
        fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
        dlclose(dlhdl);
        return 1;
    }

    // typedef int32_t (*FunctionType)();
    // FunctionType sfdc_wave_clear = (FunctionType)dlsym(dlhdl, "sfdc_wave_clear");
    // if (!sfdc_wave_clear) {
    //     fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
    //     dlclose(dlhdl);
    //     return 1;
    // }

    typedef int32_t (*Functioncali_run)(const std::string &,
                                        const scan_config &,
                                        std::vector<app_sfdc_param> &);
    Functioncali_run sfdc_param_cali_run = (Functioncali_run)dlsym(dlhdl, "sfdc_param_cali_run");
    if (!sfdc_param_cali_run)
    {
        fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
        dlclose(dlhdl);
        return 1;
    }

    typedef int32_t (*Functiondrift_apply)(int16_t);
    Functiondrift_apply sfdc_drift_apply = (Functiondrift_apply)dlsym(dlhdl, "sfdc_drift_apply");
    if (!sfdc_drift_apply)
    {
        fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
        dlclose(dlhdl);
        return 1;
    }

    typedef int32_t (*Functionwave_play)(play_record &);
    Functionwave_play sfdc_wave_play = (Functionwave_play)dlsym(dlhdl, "sfdc_wave_play");
    if (!sfdc_wave_play)
    {
        fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
        dlclose(dlhdl);
        return 1;
    }

    typedef int32_t (*Functiontc_add)(tc_config &);
    Functiontc_add sfdc_tc_add = (Functiontc_add)dlsym(dlhdl, "sfdc_tc_add");
    if (!sfdc_tc_add)
    {
        fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
        dlclose(dlhdl);
        return 1;
    }

    typedef int32_t (*Functiontc_edit)(tc_config &);
    Functiontc_edit sfdc_tc_edit = (Functiontc_edit)dlsym(dlhdl, "sfdc_tc_edit");
    if (!sfdc_tc_edit)
    {
        fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
        dlclose(dlhdl);
        return 1;
    }

    typedef int32_t (*Functiontc_excute)(const std::string &, const std::string &, std::vector<play_record> &);
    Functiontc_excute sfdc_tc_excute = (Functiontc_excute)dlsym(dlhdl, "sfdc_tc_excute");
    if (!sfdc_tc_excute)
    {
        fprintf(stderr, "Failed to get the function pointer: %s\n", dlerror());
        dlclose(dlhdl);
        return 1;
    }

    // 调用函数
    // char wave_name[64] = "/system/vendor/test_wave.txt";
    std::string wave_name = "/system/vendor/test_wave.txt";
    std::vector<int8_t> wave_data = {0, 1, 2, 3};
    printf("%s\n", wave_name.c_str());
    val = sfdc_wave_add(wave_name, 0, wave_data);
    printf("%d\n", val);

    // val = sfdc_wave_clear();
    // printf("%d\n", val);

    struct scan_config config =
        {
            .freq_step = 1,
            .freq_window = 10,
        };
    std::vector<app_sfdc_param> param_list;
    val = sfdc_param_cali_run("SLA0809A", config, param_list);
    printf("%d\n", val);
    for (int i = 0; i < param_list.size(); i++)
    {
        printf("avg_slope = %f, wave_fc = %f, bemf_offset = %f \n", param_list[i].avg_slope, param_list[i].wave_fc, param_list[i].bemf_offset);
    }

    // val = sfdc_drift_apply(10);
    // printf("%d\n", val);

    struct tc_wave_config wave1 =
        {
            .wave_index = 0,
            .f0_drift = 5,
            .playback_count = 5,
            .playback_interval = 1,
        };
    
    struct tc_config tc1 =
        {
            .tc_name = "test1",
            .valid_wave_count = 1,
            .wave_config[0] = wave1,
            .repeat_loop = 5,
            .record_interval = 1000,
        };

    val = sfdc_tc_add(tc1);
    printf("%d\n", val);

    tc1.repeat_loop = 1;
    tc1.record_interval = 5;
    val = sfdc_tc_edit(tc1);
    printf("%d\n", val);

    // tc_record record = {0, {}};
    // record.play_record_list.resize(100);
    // val = sfdc_tc_excute(tc1.tc_name, record);

    std::vector<play_record> play_record_list;
    std::string record_path = "/vendor/record.txt";
    val = sfdc_tc_excute(record_path, tc1.tc_name, play_record_list);
    printf("%d\n", val);
    for (int i = 0; i < play_record_list.size(); i++)
    {
        printf("play times = %d, output f0 = %f, relative bemf = %f \n", (int)play_record_list.size(), play_record_list[i].output_f0,
               play_record_list[i].relative_bemf);
    }

    // struct play_record record;
    // for(int i=0;i<10;i++)
    // {
    //     usleep(1000000);
    //     sfdc_wave_play(record);
    //     printf("play times = %d, output f0 = %f, relative bemf = %f \n", i, record.output_f0, record.relative_bemf);
    // }

    dlclose(dlhdl);
    return 0;
}
