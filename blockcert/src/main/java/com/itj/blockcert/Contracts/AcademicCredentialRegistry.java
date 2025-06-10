package com.itj.blockcert.Contracts;


import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Simplified Web3j wrapper for the AcademicCredentialRegistry contract
 */
public class AcademicCredentialRegistry extends Contract {
    // For a real application, you should use a proper generated wrapper
    // This is a simplified version for demonstration

    public static final String FUNC_ISSUECREDENTIAL = "issueCredential";
    public static final String FUNC_VERIFYCREDENTIALBYHASH = "verifyCredentialByHash";

    @Deprecated
    protected AcademicCredentialRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected AcademicCredentialRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    protected AcademicCredentialRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    // Replace with your actual contract binary from Solidity compilation
    private static final String BINARY = "0x608060405234801561001057600080fd5b50600080546001600160a01b031916331790556001805460ff60a01b1916600160a01b1790556102cf806100456000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80633b0a49a614610046578063b4f40c611461007e578063f14fcbc8146100a1575b600080fd5b6100696004803603602081101561005c57600080fd5b50356001600160a01b03166100c4565b6040805161ffff9092168252519081900360200190f35b6100a16004803603602081101561009457600080fd5b50356001600160a01b03166100d7565b005b6100a1600480360360208110156100b757600080fd5b50356001600160a01b0316610158565b60006020819052908152604090205461ffff1681565b6000546001600160a01b031633146100ee57600080fd5b6001600160a01b03811660009081526020819052604090205461ffff16156101155761011561024e565b6001600160a01b038116600090815260208190526040902080546001919061ffff191682805b021790555050565b6000546001600160a01b0316331461016f57600080fd5b6001600160a01b03811660009081526020819052604090205460ff161561019557600080fd5b6001600160a01b038116600090815260208190526040902054610100900460ff16156101bf576101bf61024e565b6001600160a01b038116600090815260208190526040902080546001919060ff191682801515810260ff60f81b1984831617908190556101136102065750505050505050565b6000546001600160a01b031633141561021e5761021e61024e565b6001600160a01b03166000908152602081905260409020805460ff191660011790556102266101ff565b60405160e11b62461bcd0281526004018080602001828103825260238152602001806102776023913960400191505060405180910390fd5b6040516001600160a01b038216907f6719d08c1888103bea251a4ed56406bd0c3e69723c8a1686e017e7bbe159b6f890600090a25056fe496e73756666696369656e7420617574686f72697a6174696f6e20666f722072657175657374a26469706673582212209c6ef38f60883953ef0fd782dc40266f61e206d694e22cfe238277318764cf9364736f6c63430006040033";

    public static AcademicCredentialRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new AcademicCredentialRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> issueCredential(String studentId, String ipfsCID, String fileHash) {
        final Function function = new Function(
                FUNC_ISSUECREDENTIAL, 
                Arrays.asList(
                        new Utf8String(studentId),
                        new Utf8String(ipfsCID),
                        new Utf8String(fileHash)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> verifyCredentialByHash(String fileHash) {
        final Function function = new Function(
                FUNC_VERIFYCREDENTIALBYHASH, 
                Arrays.asList(new Utf8String(fileHash)),
                Arrays.asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }
}
