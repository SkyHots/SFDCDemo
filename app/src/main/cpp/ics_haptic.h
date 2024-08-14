#ifndef ICS_HAPTIC_LIB_H
#define ICS_HAPTIC_LIB_H

#include <stdint.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <dlfcn.h>
#include <stdint.h>
#include <vector>
#include <string>

struct scan_config {
    float freq_step;
    float freq_window;
};

struct app_sfdc_param {
    float lra_f0;
    float wave_fc;
    float avg_slope;
    float bemf_offset;
    int sfdc_support;
};

struct play_record {
    uint16_t wave_index;
    float temperature;
    float output_f0;
    float relative_bemf;
};

struct tc_wave_config {
    uint16_t wave_index;
    int16_t f0_drift;
    uint16_t playback_count;
    float playback_interval;
};

struct tc_config {
    char tc_name[20];
    uint16_t valid_wave_count;
    struct tc_wave_config wave_config[100];
    uint16_t repeat_loop;
    uint16_t record_interval;
};

#ifdef __cplusplus
extern "C" {
#endif

float sfdc_get_continuous_f0();

int32_t sfdc_wave_clear();

int32_t sfdc_wave_add(
        const std::string &wave_name,
        const uint16_t wave_length,
        const std::vector<int8_t> &wave_data);

int32_t sfdc_param_cali_run(
        const std::string &lra_type,
        const scan_config &config,
        std::vector<app_sfdc_param> &param_list);

/*int32_t sfdc_param_cali_apply(void);*/

int32_t sfdc_drift_apply(int16_t f0_drift);

int32_t sfdc_disable();

int32_t sfdc_enable();

int32_t sfdc_wave_play(play_record &record);

int32_t sfdc_tc_add(tc_config &config);

int32_t sfdc_tc_edit(tc_config &config);

int32_t sfdc_tc_delete(const std::string &tc_name);

int32_t sfdc_tc_clear();

int32_t long_wave_play(const std::string &wave_name);

int32_t sfdc_tc_excute(const std::string &path, const std::string &tc_name, std::vector<play_record> &play_record_list);

#ifdef __cplusplus
}
#endif // __cplusplus

#endif // ICS_HAPTIC_LIB_H

