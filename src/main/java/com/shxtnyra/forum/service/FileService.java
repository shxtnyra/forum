package com.shxtnyra.forum.service;

import com.shxtnyra.forum.entity.FileEntity;
import com.shxtnyra.forum.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    // Метод для сохранения файла
    ///
    /// Перед сохранением файла так-то надо добавить валидацию, на максимальный размер файла или допустимые типы файлов, 
    /// но я пока не знаю, что у нас и как поэтому пока оставлю так
    /// 
    /// 
    public FileEntity saveFile(FileEntity fileEntity) {
        try {
            return fileRepository.save(fileEntity);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
            return null;
        }
    }

    // Метод для получения файла по идентификатору
    public Optional<FileEntity> getFileById(Long id) {
        try {
            return fileRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Ошибка при получении файла с идентификатором " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public String getFileAsText(Long id) {
        Optional<FileEntity> fileEntityOptional = getFileById(id);
        if (fileEntityOptional.isPresent()) {
            return encodeFileToBase64(fileEntityOptional.get());
        } else {
            System.err.println("Файл с идентификатором " + id + " не найден.");
            return null;
        }
    }

    //метод для кодирования файла в текстовое представление
    private String encodeFileToBase64(FileEntity fileEntity) {
        try {
            return Base64.getEncoder().encodeToString(fileEntity.getData());
        } catch (Exception e) {
            System.err.println("Ошибка при получении файла в текстовом представлении: " + e.getMessage());
            return null;
        }
    }
}
