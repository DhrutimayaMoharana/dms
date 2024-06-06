package com.watsoo.dms.serviceimp;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.watsoo.dms.dto.CommandDto;
import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.dto.PaginatedResponseDto;
import com.watsoo.dms.dto.Response;
import com.watsoo.dms.entity.Command;
import com.watsoo.dms.entity.CommandType;
import com.watsoo.dms.repository.CommandRepository;
import com.watsoo.dms.repository.CommandTypeRepository;
import com.watsoo.dms.service.CommandService;
import com.watsoo.dms.validation.Validation;

@Service
public class CommandServiceImpl implements CommandService {

	@Autowired
	private CommandRepository commandRepository;

	@Autowired
	private CommandTypeRepository commandTypeRepository;

	@Override
	public Response<?> getAllCommands(int pageSize, int pageNo, String deviceModel, Long vechileId, String imeiNumber) {
		try {
			PaginatedRequestDto paginatedRequest = new PaginatedRequestDto(pageSize, pageNo, deviceModel, vechileId,
					imeiNumber);
			Pageable pageable = pageSize > 0 ? PageRequest.of(pageNo, pageSize) : Pageable.unpaged();
			Page<Command> commandPage = commandRepository.findAll(paginatedRequest, pageable);
			List<CommandDto> listOfCommand = commandPage.getContent().stream().map(CommandDto::entityToDto)
					.collect(Collectors.toList());

			PaginatedResponseDto<Object> paginatedResponseDto = new PaginatedResponseDto<>(commandRepository.count(),
					listOfCommand.size(), commandPage.getTotalPages(), pageNo, listOfCommand);

			return new Response<>("Success To featch command", paginatedResponseDto, 200);
		} catch (Exception e) {
			return new Response<>("Something Went Wrong", null, 400);
		}
	}

	@Override
	public Response<?> getCommandById(Long id) {
		return new Response<>("Success", commandRepository.findById(id), 200);
	}

	@Override
	public Response<?> createCommand(CommandDto commandDto) {

		Response<?> validateCommandDto = Validation.validateCommandDto(commandDto);
		if (validateCommandDto != null) {
			return validateCommandDto;
		}
		Command findByCommandAndVechileId = commandRepository.findByCommandAndVechileId(commandDto.getBaseCommand(),
				commandDto.getVechile_id());

		if (findByCommandAndVechileId != null) {
			return new Response<>("Command Already Exit ", null, 400);
		}

		Command command = CommandDto.dtoToEntity(commandDto);
		commandRepository.save(command);
		return new Response<>("Success", null, 200);

	}

	@Override
	public Response<?> updateCommand(Long id, CommandDto command) {
		return null;
	}

}
