using namespace linalg;

StructuredBuffer<half> A;
StructuredBuffer<half> B;
RWStructuredBuffer<float> C;

typealias MatA = CoopMat<half, MemoryScope.Subgroup, 16, 16, CoopMatMatrixUse::MatrixA>;
typealias MatB = CoopMat<half, MemoryScope.Subgroup, 16, 16, CoopMatMatrixUse::MatrixB>;
typealias MatAcc = CoopMat<float, MemoryScope.Subgroup, 16, 16, CoopMatMatrixUse::MatrixAccumulator>;

[shader("compute")]
[numthreads(32, 1, 1)]
void matmul()
{
    let stride = 16;

    MatA a = MatA.Load<CoopMatMatrixLayout::RowMajor>(A, 0, stride);
    MatB b = MatB.Load<CoopMatMatrixLayout::RowMajor>(B, 0, stride);

    MatAcc acc = MatAcc(0.0f);

    acc = coopMatMulAdd<float, false>(a, b, acc);

    acc.Store<CoopMatMatrixLayout::RowMajor>(C, 0, stride);
}
