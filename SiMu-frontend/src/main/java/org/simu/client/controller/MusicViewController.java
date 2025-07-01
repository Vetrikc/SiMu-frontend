package org.simu.client.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.simu.client.entity.Music;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class MusicViewController {

    @Autowired
    RestTemplate restTemplate;

    public MusicViewController() {
    }


    @RequestMapping("/")
    public String showFirstView() {
        return "first-view";
    }


    @RequestMapping("/musics")
    public String showMusicPage(Model model) {

        String apiUrl = "http://localhost:8080/api/musics";
        //  ResponseEntity<List<Music>> response = restTemplate.getForEntity(apiUrl,List.class);// Получение одного объекта
        List<Music> response = restTemplate.exchange(apiUrl, HttpMethod.GET,null, new ParameterizedTypeReference<List<Music>>(){
        }).getBody();


        model.addAttribute("listMusics", response);

        System.out.println(response);
        return "musicList"; // JSP-файл
    }

    @RequestMapping("/upload")
    public String handleFileUpload(@ModelAttribute("music") Music music, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
        System.out.println(music);
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file");
            model.addAttribute("music", music);
            return "redirect:/uploadForm";
        }

        // Создание данных для запроса
        try {
            // Заголовки запроса
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Формируем тело запроса
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("multipartFile", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); // Имя файла
                }
            });
            body.add("music", music);
            // Упаковываем заголовки и тело в HttpEntity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // URL конечной точки REST API
            String url = "http://localhost:8080/api/musics";
            // Отправляем запрос
            String response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            if (response != null) {
                redirectAttributes.addFlashAttribute("message", "Файл успешно загружен!");
                return "redirect:/uploadForm";
            } else {
                redirectAttributes.addFlashAttribute("message", "Выберите файл для загрузки!");
                return "redirect:/uploadForm";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/musics";
    }

    @RequestMapping("/uploadForm")
    public String handleFileUpload1(Model model) {
        Music music = new Music();
        model.addAttribute(music);
        return "upload-musics";
    }
    @RequestMapping("/app")
    public String app() {
        return "musicPlayer";
    }


}
